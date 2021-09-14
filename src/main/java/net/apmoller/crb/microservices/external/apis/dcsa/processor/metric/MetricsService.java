package net.apmoller.crb.microservices.external.apis.dcsa.processor.metric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.*;

@Service
@NoArgsConstructor
@Slf4j
public class MetricsService {
        @Autowired(required = false)
    private MeterRegistry meterRegistry;

    public Mono<Void> incrementRecievedGemsEvent() {
        return incrementTallyWithTagAndCounterName("", EXTERNAL_RECIEVE_DCSA_GEMS_EVENT, DUMMY_VALUE);
    }

    public Mono<Void> incrementRecievedEventType(String senderName) {
        return incrementTallyWithTagAndCounterName(senderName, EXTERNAL_RECIEVE_DCSA_EVENT_TYPE, DUMMY_VALUE);
    }
    public Mono<Void> incrementRecievedDroppedEvent() {
        return incrementTallyWithTagAndCounterName("", EXTERNAL_DCSA_EVENT_DROPPED, DUMMY_VALUE);
    }
    public Mono<Void> incrementRecievedErrorEvent() {
        return incrementTallyWithTagAndCounterName("", EXTERNAL_DCSA_EVENT_ERROR, DUMMY_VALUE);
    }
    public Mono<Void> incrementSentKafkaMessage() {
        return incrementTallyWithTagAndCounterName("", EXTERNAL_RECIEVE_KAFKA_MESSAGE, DUMMY_VALUE);
    }

    public Mono<Void> incrementErrorKafka() {
        return incrementTallyWithTagAndCounterName("", EXTERNAL_KAFKA_ERROR, DUMMY_VALUE);
    }

    public Mono<Void> incrementTallyWithTagAndCounterName(String tagValue, String counterName, String tagName) {
        return Mono.just(tagValue)
                .doOnNext(value -> meterRegistry.counter(counterName, tagName, value).increment())
                .doOnError(exception -> logError(counterName, exception.getMessage()))
                .onErrorReturn("")
                .then();
    }

    private void logError(String counterName, String failureMessage) {
        log.error("Metrics could not be sent for the counter {} from external-dcsa due to {}", counterName, failureMessage);
    }

}
