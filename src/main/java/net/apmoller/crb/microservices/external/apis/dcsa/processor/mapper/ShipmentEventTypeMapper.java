package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.ShipmentEventType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import org.springframework.stereotype.Component;

import static MSK.com.external.dcsa.ShipmentEventType.CONF;
import static MSK.com.external.dcsa.ShipmentEventType.DRFT;
import static MSK.com.external.dcsa.ShipmentEventType.ISSU;
import static MSK.com.external.dcsa.ShipmentEventType.PENA;
import static MSK.com.external.dcsa.ShipmentEventType.RECE;
import static MSK.com.external.dcsa.ShipmentEventType.REJE;
import static MSK.com.external.dcsa.ShipmentEventType.SURR;

@Component
public class ShipmentEventTypeMapper {

    public ShipmentEventType asShipmentEventType(String eventAct) {

        switch (eventAct) {
            case "Arrange_Cargo_Release_Closed":
            case "Confirm_Shipment_Closed":
                return CONF;
            case "Arrange_Cargo_Release_Open":
                return PENA;
            case "Issue_Original_TPDOC_Closed":
            case "ARRIVAL_NOTICE":
                return ISSU;
            case "Issue_Verify_Copy_of_TPDOC_Closed":
                return DRFT;
            case "Receive_Transport_Document_Instructions_Closed":
                return RECE;
            case "RELEASE":
                return SURR;
            case "Shipment_Cancelled":
                return REJE;
            default:
                throw new MappingException("Could not map Shipment Event Type of ".concat(eventAct));
        }
    }
}
