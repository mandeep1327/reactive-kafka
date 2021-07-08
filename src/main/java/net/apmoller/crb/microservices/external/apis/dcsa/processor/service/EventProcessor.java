package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.ReactiveDcsaRepository;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProcessor {

    private final ReactiveDcsaRepository repository;

    public Flux<Event> findEvents() {
        return repository.findAll();
    }



}
