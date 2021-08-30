package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.ShipmentInformationType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import org.springframework.stereotype.Component;

import static MSK.com.external.dcsa.ShipmentInformationType.ARN;
import static MSK.com.external.dcsa.ShipmentInformationType.BOK;
import static MSK.com.external.dcsa.ShipmentInformationType.SHI;
import static MSK.com.external.dcsa.ShipmentInformationType.SRM;
import static MSK.com.external.dcsa.ShipmentInformationType.TRD;

@Component
public class ShipmentInformationTypeMapper {

    public ShipmentInformationType asShipmentInformationTypeCode(String eventAct) {

        switch (eventAct) {
            case "Arrange_Cargo_Release_Closed":
            case "Arrange_Cargo_Release_Open":
                return SRM;
            case "Confirm_Shipment_Closed":
            case "Shipment_Cancelled":
                return BOK;
            case "Issue_Original_TPDOC_Closed":
            case "Issue_Verify_Copy_of_TPDOC_Closed":
            case "RELEASE":
                return TRD;
            case "Receive_Transport_Document_Instructions_Closed":
                return SHI;
            case "ARRIVAL_NOTICE":
                return ARN;
            default:
                throw new MappingException("Could not map Shipment Information Type Code ".concat(eventAct));
        }
    }
}
