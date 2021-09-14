package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import MSK.com.gems.GEMSPubType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricsService;


@Service
@Slf4j
@RequiredArgsConstructor
public class GEMSPubTypeKafkaConsumer {

    private final KafkaReceiver<String, GEMSPubType> kafkaReceiver;
    private final EventDelegator eventDelegator;
    private final MetricsService metricsService;

    @EventListener(ApplicationStartedEvent.class)
    public Disposable startKafkaConsumer() {
        return kafkaReceiver
                .receive()
                .doOnNext(gemsRecord -> log.info("Received event: key {}", gemsRecord.key()))
                .doOnError(error -> log.error("Error receiving shipment record", error))
                .flatMap(this::handleSubscriptionResponseEvent)
                .doOnNext(gemsRecord -> gemsRecord.receiverOffset().acknowledge())
                .subscribe();
    }

    private Mono<ReceiverRecord<String, GEMSPubType>> handleSubscriptionResponseEvent(ReceiverRecord<String, GEMSPubType> gemsRecord) {
        return Mono.just(gemsRecord)
                .map(ConsumerRecord::value)
                .flatMap(this::handleSubscriptionResponse)
                .doOnError(ex -> logGemsError(gemsRecord,ex))
                .onErrorResume(ex -> Mono.empty())
                .doOnNext(event -> log.info("Successfully processed event"))
                .then(Mono.just(gemsRecord));
    }

    private Mono<Void> handleSubscriptionResponse(GEMSPubType gemsPubType) {
        log.info("GEMS Event received:::: {}", gemsPubType);
        return Mono.just(gemsPubType)
                .doOnNext(dummy -> metricsService.incrementRecievedGemsEvent().subscribe())
                .doOnNext(gemsPubType1 -> eventDelegator.checkCorrectEvent(gemsPubType))
                .then();
    }

    private void logGemsError(ReceiverRecord<String, GEMSPubType> gemsRecord, Throwable ex){
        log.warn("Error processing event {}", gemsRecord.key(), ex);
        metricsService.incrementRecievedErrorEvent().subscribe();

    }


}
