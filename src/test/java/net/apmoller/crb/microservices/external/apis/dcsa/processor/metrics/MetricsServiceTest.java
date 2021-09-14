package net.apmoller.crb.microservices.external.apis.dcsa.processor.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.EXTERNAL_KAFKA_ERROR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.EXTERNAL_DCSA_EVENT_ERROR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.EXTERNAL_RECIEVE_DCSA_EVENT_TYPE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.EXTERNAL_RECIEVE_DCSA_GEMS_EVENT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.EXTERNAL_RECIEVE_KAFKA_MESSAGE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.EXTERNAL_DCSA_EVENT_DROPPED;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MetricsServiceTest {
    public static final String DUMMY = "default_value";

    @Mock
    private MeterRegistry meterRegistry;
    @InjectMocks
    private MetricsService unit;
    private final Counter mockCounter = mock(Counter.class);

    @BeforeEach
    void init() {
        when(meterRegistry.counter(any(), any(), any())).thenReturn(mockCounter);
    }

    @Test
    void incrementRecievedGemsCounter() {
        StepVerifier.create(unit.incrementRecievedGemsEvent()).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_RECIEVE_DCSA_GEMS_EVENT, DUMMY,"");
    }

    @Test
    void incrementRecievedKafkaCounter() {
        StepVerifier.create(unit.incrementSentKafkaMessage()).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_RECIEVE_KAFKA_MESSAGE, DUMMY,"");
    }

    @Test
    void incrementDroppedEventCounter() {
        StepVerifier.create(unit.incrementRecievedDroppedEvent()).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_DCSA_EVENT_DROPPED, DUMMY,"");
    }

    @Test
    void incrementErrorEventCounter() {
        StepVerifier.create(unit.incrementRecievedErrorEvent()).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_DCSA_EVENT_ERROR, DUMMY,"");
    }

    @Test
    void incrementKafkaEventCounter() {
        StepVerifier.create(unit.incrementErrorKafka()).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_KAFKA_ERROR, DUMMY,"");
    }

    @Test
    void incrementEventTypeCounter() {
        StepVerifier.create(unit.incrementRecievedEventType("shipment")).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_RECIEVE_DCSA_EVENT_TYPE, DUMMY,"shipment");
    }
    @Test
    void incrementEventTypeTransportCounter() {
        StepVerifier.create(unit.incrementRecievedEventType("transport")).verifyComplete();
        thenCounterWithSenderTagIncremented(EXTERNAL_RECIEVE_DCSA_EVENT_TYPE, DUMMY,"transport");
    }

    @Test
    void incrementCreationRequestsCounterWhenExceptionThrown() {
        mockExceptionThrown();

        StepVerifier.create(unit.incrementRecievedGemsEvent()).verifyComplete();

        thenCounterWithSenderTagIncremented(EXTERNAL_RECIEVE_DCSA_GEMS_EVENT, DUMMY,"");
    }

    private void mockExceptionThrown() {
        doThrow(IllegalArgumentException.class).when(mockCounter).increment();
    }

    private void thenCounterWithSenderTagIncremented(String metricName, String tagName, String tagValue) {
        verify(meterRegistry).counter(metricName, tagName, tagValue);
        verify(mockCounter).increment();
    }
}
