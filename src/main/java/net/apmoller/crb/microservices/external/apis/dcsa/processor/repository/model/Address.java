package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Address {
    private String addressName;
    private String streetName;
    private String streetNumber;
    private String floor;
    private String postCode;
    private String cityName;
    private String stateRegion;
    private String country;
}
