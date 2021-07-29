package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ShipmentEvent extends Event {


    public enum ShipmentEventType{
        RECE, DRFT, PENA, PENU, REJE, APPR, ISSU, SURR, SUBM, VOID, CONF
    }

    public enum ShipmentInformationTypeCode{
        BOK, SHI, VGM ,SRM, TRD, ARN
    }

    private ShipmentInformationTypeCode documentTypeCode;
    private ShipmentEventType shipmentEventTypeCode;
    private String documentID;
    private String reason;
}
