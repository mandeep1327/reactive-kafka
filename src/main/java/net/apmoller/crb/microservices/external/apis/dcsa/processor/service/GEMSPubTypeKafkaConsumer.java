package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import com.maersk.jaxb.pojo.GEMSPubType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GEMSPubTypeKafkaConsumer {

    private final KafkaReceiver<String, GEMSPubType> kafkaReceiver;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return kafkaReceiver
                .receive()
                .doOnNext(record -> log.info("Received event: key {}, value {}", record.key(), record.value()))
                .doOnError(error -> log.error("Error receiving shipment record", error))
                .flatMap(this::handleSubscriptionResponseEvent)
                .doOnNext(record -> record.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<ReceiverRecord<String, GEMSPubType>> handleSubscriptionResponseEvent(ReceiverRecord<String, GEMSPubType> record) {
        try {
            return Mono.just(record)
                    .filter(this::filterDeserializationError)
                    .map(ConsumerRecord::value)
                    .flatMap(this::handleSubscriptionResponse)
                    .doOnError(ex -> log.warn("Error processing event {}", record.key(), ex))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                    .doOnError(ex -> log.error("Error processing event after all retries {}", record.key(), ex))
                    .onErrorResume(ex -> Mono.empty())
                    .doOnNext(__ -> log.info("Successfully processed event"))
                    .then(Mono.just(record));
        } catch (Exception ex) {
            log.error("Error processing event {}", record.key(), ex);
            return Mono.just(record);
        }
    }

    private boolean filterDeserializationError(ReceiverRecord<String, GEMSPubType> record) {
        byte[] keyDeserializationException =
                Optional.ofNullable(record.headers().lastHeader(ErrorHandlingDeserializer.KEY_DESERIALIZER_EXCEPTION_HEADER))
                        .map(Header::value)
                        .orElse(null);
        if (keyDeserializationException != null) {
            log.error("Failed to deserialize key", deserializeExceptionObject(keyDeserializationException));
            return false;
        }
        byte[] valueDeserializationException = Optional
                .ofNullable(record.headers().lastHeader(ErrorHandlingDeserializer.VALUE_DESERIALIZER_EXCEPTION_HEADER))
                .map(Header::value)
                .orElse(null);
        if (valueDeserializationException != null) {
            log.error("Failed to deserialize value", deserializeExceptionObject(valueDeserializationException));
            return false;
        }
        return true;
    }

    private Exception deserializeExceptionObject(byte[] exception) {
        try (var bais = new ByteArrayInputStream(exception)) {
            try (var ois = new ObjectInputStream(bais)) {
                return (Exception) ois.readObject();
            } catch (ClassNotFoundException ex) {
                return null;
            }
        } catch (IOException ex) {
            return null;
        }
    }

    private Mono<Void> handleSubscriptionResponse(GEMSPubType gemsPubType) {
        log.info("GEMS Event received:::: {}", gemsPubType);
        return Mono.empty()
                .then();
    }


}
