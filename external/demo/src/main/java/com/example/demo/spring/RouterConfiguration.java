package com.example.demo.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class RouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> route(Handler handler) {
        return RouterFunctions.route(POST("/shipment"), handler::submitShipment);

    }
}
