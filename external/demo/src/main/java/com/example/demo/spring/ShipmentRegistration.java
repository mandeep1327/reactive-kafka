package com.example.demo.spring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentRegistration {

    private String source;
    private String destination;
    private double weight;
    private Instant shipmentDate;
    private Integer id;

}
