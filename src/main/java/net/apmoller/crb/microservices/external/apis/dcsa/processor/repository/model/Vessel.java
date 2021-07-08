package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Vessel {
    private Integer vesselIMONumber;
    private String vesselName;
    private String vesselFlag;
    private String vesselCallSignNumber;
    private String vesselOperatorCarrierId;
}
