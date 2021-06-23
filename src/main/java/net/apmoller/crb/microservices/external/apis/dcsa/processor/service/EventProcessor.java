package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.ReactiveDcsaRepository;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final ReactiveDcsaRepository repository;

    public Flux<Event> findEvents() {
        return repository.findAll();
    }

    public Mono<Event> processEvents() {
        Event testEvent = createEvent();
        return repository.save(testEvent);
    }

    private Event createEvent() {
        return Event.builder()
                .eventID("1")
                .eventType("SHIPMENT")
                .eventDateTime("2020-01-01T00:00:00")
                .eventCreatedDateTime("2020-01-01T00:00:00")
                .bookingReference("ABC123")
                .eventClassifierCode("ACT")
                .build();
    }

}
