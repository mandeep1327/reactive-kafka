package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShipmentEvent extends Event {

    private enum ShipmentEventType{
        RECE, DRFT, PENA, PENU, REJE, APPR, ISSU, SURR, SUBM, VOID, CONF
    }

    private enum ShipmentInformationTypeCode{
        BOK, SHI, VGM ,SRM, TRD, ARN;
    }

    private ShipmentInformationTypeCode shipmentInformationTypeCode;
    private ShipmentEventType shipmentEventType;
    private String documentID;
    private String reason;
}
