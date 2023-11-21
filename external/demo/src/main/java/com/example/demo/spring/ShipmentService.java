package com.example.demo.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShipmentService     {
    private final ShipmentRepository shipmentRepository;

    public Mono<Shipment> submit(ShipmentRegistration shipmentRegistration){
        Shipment buildShipmentRequest = buildRequest(shipmentRegistration);
       return shipmentRepository.save(buildShipmentRequest);
    }
    public Shipment buildRequest(ShipmentRegistration shipmentRegistration){
        return Shipment.builder().name(shipmentRegistration.getDestination()).build();
    }
}
