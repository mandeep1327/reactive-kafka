package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.MappingException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class DocumentIdMapper {

    public String asDocumentId(PubSetType pubSetType) {
        switch (pubSetType.getEvent().getEventAct()) {
            case "Confirm_Shipment_Closed":
            case "Shipment_Cancelled":
                return getDocumentIdForBookingEvents(pubSetType);
            case "Arrange_Cargo_Release_Closed":
            case "Arrange_Cargo_Release_Open":
            case "Issue_Original_TPDOC_Closed":
            case "Issue_Verify_Copy_of_TPDOC_Closed":
            case "RELEASE":
            case "Receive_Transport_Document_Instructions_Closed":
            case "ARRIVAL_NOTICE":
                return getDocumentIdForOthers(pubSetType);
            default:
                throw new MappingException("Booking Number is not available for Document ID");
        }
    }

    @NotNull
    private String getDocumentIdForOthers(PubSetType pubSetType) {
        if (!isNull(pubSetType.getTpdoc()) &&
                !pubSetType.getTpdoc().isEmpty() &&
                !isNull(pubSetType.getTpdoc().get(0).getBolNo())) {
            return pubSetType.getTpdoc().get(0).getBolNo();
        }
        throw new MappingException("Booking Number is not available for Document ID");
    }

    @NotNull
    private String getDocumentIdForBookingEvents(PubSetType pubSetType) {
        if (!isNull(pubSetType.getShipment()) && !isNull(pubSetType.getShipment().getBookNo())) {
            return pubSetType.getShipment().getBookNo();
        }
        throw new MappingException("Booking Number is not available for Document ID");
    }
}
