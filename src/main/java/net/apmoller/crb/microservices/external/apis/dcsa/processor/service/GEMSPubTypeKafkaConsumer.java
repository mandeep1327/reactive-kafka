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
    private final EventDelegator eventDelegator;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return kafkaReceiver
                .receive()
                .doOnNext(gemsRecord -> log.info("Received event: key {}, value {}", gemsRecord.key(), gemsRecord.value()))
                .doOnError(error -> log.error("Error receiving shipment record", error))
                .flatMap(this::handleSubscriptionResponseEvent)
                .doOnNext(gemsRecord -> gemsRecord.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<ReceiverRecord<String, GEMSPubType>> handleSubscriptionResponseEvent(ReceiverRecord<String, GEMSPubType> gemsRecord) {
        try {
            return Mono.just(gemsRecord)
                    .filter(this::filterDeserializationError)
                    .map(ConsumerRecord::value)
                    .flatMap(this::handleSubscriptionResponse)
                    .doOnError(ex -> log.warn("Error processing event {}", gemsRecord.key(), ex))
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(5)))
                    .doOnError(ex -> log.error("Error processing event after all retries {}", gemsRecord.key(), ex))
                    .onErrorResume(ex -> Mono.empty())
                    .doOnNext(event -> log.info("Successfully processed event"))
                    .then(Mono.just(gemsRecord));
        } catch (Exception ex) {
            log.error("Error processing event {}", gemsRecord.key(), ex);
            return Mono.just(gemsRecord);
        }
    }

    private boolean filterDeserializationError(ReceiverRecord<String, GEMSPubType> gemsRecord) {
        byte[] keyDeserializationException =
                Optional.ofNullable(gemsRecord.headers().lastHeader(ErrorHandlingDeserializer.KEY_DESERIALIZER_EXCEPTION_HEADER))
                        .map(Header::value)
                        .orElse(null);
        if (keyDeserializationException != null) {
            log.error("Failed to deserialize key", deserializeExceptionObject(keyDeserializationException));
            return false;
        }
        byte[] valueDeserializationException = Optional
                .ofNullable(gemsRecord.headers().lastHeader(ErrorHandlingDeserializer.VALUE_DESERIALIZER_EXCEPTION_HEADER))
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
            return getException(bais);
        } catch (IOException ex) {
            return null;
        }
    }

    private Exception getException(ByteArrayInputStream bais) throws IOException {
        try (var ois = new ObjectInputStream(bais)) {
            return (Exception) ois.readObject();
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private Mono<Void> handleSubscriptionResponse(GEMSPubType gemsPubType) {
        log.info("GEMS Event received:::: {}", gemsPubType);
        return Mono.just(gemsPubType)
                .map(gemsPubType1 -> eventDelegator.checkCorrectEvent(gemsPubType))
                .doOnError(exception -> log.error(exception.getMessage()))
                .then();
    }


}
