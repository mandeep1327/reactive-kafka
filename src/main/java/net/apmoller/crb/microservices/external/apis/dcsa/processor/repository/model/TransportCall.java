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

    public enum FacilityTypeCode {
        BOCR, CLOC, COFS, COYA, OFFD, DEPO, INTE, POTE, PBPL, BRTH
    }

    public enum TransPortMode {
        VESSEL, RAIL, TRUCK, BARGE
    }


    private String carrierServiceCode;
    private String carrierVoyageNumber;
    private FacilityTypeCode facilityTypeCode;
    private String otherFacility;
    private TransPortMode modeOfTransport;
    private String carrierCode;

    //Not specified in Excel
    private String facilityCode;
    private Integer transportCallSequenceNumber;

   //These should be part of the Get service
    private String unLocationCode;
    private Location location;
    private Vessel vessel;


}
