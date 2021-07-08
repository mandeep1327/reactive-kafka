package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransportCall {

    private enum FacilityTypeCode {
        BOCR, CLOC, COFS, COYA, OFFD, DEPO, INTE, POTE, PBPL, BRTH
    }

    private enum TransPortMode {
        VESSEL, RAIL, TRUCK, BARG
    }

    private Integer transportCallSequenceNumber;
    private String carrierServiceCode;
    private String carrierVoyageNumber;
    private String facilityCode;
    private FacilityTypeCode facilityTypeCode;
    private String otherFacility;
    private TransPortMode modeOfTransport;
    private Location location;
    private Vessel vessel;

}
