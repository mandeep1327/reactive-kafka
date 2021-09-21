package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.List;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVECUIMPNEventAct;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

 class GEMSPubTypeKafkaConsumerTest {
    private final KafkaReceiver kafkaReceiver = mock(KafkaReceiver.class);
    private final EventDelegator eventDelegator = mock(EventDelegator.class);
    private final MetricsService metricsService = mock(MetricsService.class);

    private final GEMSPubTypeKafkaConsumer kafkaConsumer = new GEMSPubTypeKafkaConsumer(kafkaReceiver, eventDelegator,metricsService);

    @Test
    void testReceiverWhenEmptyResultIsConsumed() {
        when(kafkaReceiver.receive()).thenReturn(Flux.empty());
        assertTrue(kafkaConsumer.startKafkaConsumer().isDisposed());
    }


    @Test
    void testReceiverWhenExceptionOccurs() {
        when(kafkaReceiver.receive()).thenReturn(Flux.error(new RuntimeException()));
        assertTrue(kafkaConsumer.startKafkaConsumer().isDisposed());
    }

    @Test
    void testReceiverWhenKeyIsNull() {
        var gemsData = getGemsData(List.of(getPubSetTypeWithARRIVECUIMPNEventAct()));
        var consumerRecord = new ConsumerRecord("dumyy-topic", 0, 0, null, gemsData);
        var receiverOffset = mock(ReceiverOffset.class);
        var record = new ReceiverRecord(consumerRecord, receiverOffset);
        when(kafkaReceiver.receive()).thenReturn(Flux.just(record));
        assertTrue(kafkaConsumer.startKafkaConsumer().isDisposed());
    }


    @Test
    void testReceiverWhenKeyAndValueIsMapped() {
        var gemsData = getGemsData(List.of(getPubSetTypeWithARRIVECUIMPNEventAct()));
        var consumerRecord = new ConsumerRecord("dumyy-topic", 0, 0, "dummy" , gemsData);
        var receiverOffset = mock(ReceiverOffset.class);
        var record = new ReceiverRecord(consumerRecord, receiverOffset);
        when(kafkaReceiver.receive()).thenReturn(Flux.just(record));
        assertTrue(kafkaConsumer.startKafkaConsumer().isDisposed());
    }


}
