package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class Seals {

    public enum SealSourceEnum {
        CAR, SHI, PHI, VET, CUS
    }

    private String sealNumber;
    private SealSourceEnum sealSource;
}
