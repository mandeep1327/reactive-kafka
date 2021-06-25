package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository;

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactiveDcsaRepository extends ReactiveCosmosRepository<Event, String> {

    Flux<Event> findEventsByBookingReference(String bookingReference);


}
