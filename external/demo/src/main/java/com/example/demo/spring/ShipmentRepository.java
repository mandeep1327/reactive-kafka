package com.example.demo.spring;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface ShipmentRepository extends ReactiveCrudRepository<Shipment,Integer> {
}
