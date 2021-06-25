package net.apmoller.crb.microservices.external.apis.dcsa.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.service.EventProcessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {

    private final EventProcessor processor;

    @GetMapping("/events")
    public Flux<Event> getEvents() {
        return processor.findEvents();
    }

    @GetMapping("/store")
    public Mono<Event> saveEvents() {
        return processor.processEvents();
    }
}
