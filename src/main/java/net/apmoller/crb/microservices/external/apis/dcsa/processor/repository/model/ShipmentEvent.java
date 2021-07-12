package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
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
    private List<References> references;
    private List<Party> parties;
    private String equipmentReference;
    private EquipmentEvent.CarrierCodeEnum carrierCode;
}
