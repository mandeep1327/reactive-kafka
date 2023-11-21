package com.example.demo.spring;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    ShipmentService service;

    public Mono<ServerResponse> submitShipment(ServerRequest request) {
        var response = request.bodyToMono(ShipmentRegistration.class)
                .flatMap(shipmentRegistration -> service.submit(shipmentRegistration));
        return ServerResponse.ok().body(response, ShipmentRegistration.class);
    }
}
