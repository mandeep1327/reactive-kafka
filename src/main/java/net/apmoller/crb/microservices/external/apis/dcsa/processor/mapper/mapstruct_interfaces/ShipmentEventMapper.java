package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import com.maersk.jaxb.pojo.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mapping.MappingException;

import static java.util.Objects.isNull;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.CONF;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.DRFT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.ISSU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.PENA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.RECE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.REJE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.SURR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.BOK;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.SHI;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.SRM;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.TRD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.VGM;

@Mapper(componentModel = "spring", imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class})
public interface ShipmentEventMapper {

    //TODO: reason is Missing in the mapping
    @Mapping(expression = "java(getShipmentEventType(pubSetType.getEvent().getEventAct().toString()))", target = "shipmentEventTypeCode")
    @Mapping(expression = "java(getShipmentInformationTypeCode(pubSetType.getEvent().getEventAct().toString()))", target = "documentTypeCode")
    @Mapping(expression = "java(getDocumentId(pubSetType))", target = "documentID")


    ShipmentEvent fromPubSetTypeToShipmentEvent(PubSetType pubSetType);

    default String getDocumentId (PubSetType pubSetType){
        switch (pubSetType.getEvent().getEventAct().toString()) {
            case "Confirm_Shipment_Closed":
            case "Shipment_Cancelled":
                return getDocumentIdForBookingEvents(pubSetType);
            case "Arrange_Cargo_Release_Closed":
            case "Arrange_Cargo_Release_Open":
            case "Issue_Original_TPDOC_Closed":
            case "Issue_Verify_Copy_of_TPDOC_Closed":
            case "RELEASE":
            case "Receive_Transport_Document_Instructions_Closed":
                return getDocumentIdForOthers(pubSetType);
            default:
                throw new MappingException("Booking Number is not available for Document ID");
        }
    }

    @NotNull
    private String getDocumentIdForOthers(PubSetType pubSetType) {
        if (!isNull(pubSetType.getTpdoc()) && !isNull(pubSetType.getTpdoc().get(0).getBolNo())){
            return pubSetType.getTpdoc().get(0).getBolNo().toString();
        }
        throw new MappingException("Booking Number is not available for Document ID");
    }

    @NotNull
    private String getDocumentIdForBookingEvents(PubSetType pubSetType) {
        if (!isNull(pubSetType.getShipment()) && !isNull(pubSetType.getShipment().getBookNo())){
            return pubSetType.getShipment().getBookNo().toString();
        }
        throw new MappingException("Booking Number is not available for Document ID");
    }

    default ShipmentEvent.ShipmentEventType getShipmentEventType(String eventAct) {

        switch (eventAct) {
            case "Arrange_Cargo_Release_Closed":
            case "Confirm_Shipment_Closed":
                return CONF;
            case "Arrange_Cargo_Release_Open":
                return PENA;
            case "Issue_Original_TPDOC_Closed":
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

    default ShipmentEvent.ShipmentInformationTypeCode getShipmentInformationTypeCode(String eventAct) {

        switch (eventAct) {
            case "Arrange_Cargo_Release_Closed":
            case "Arrange_Cargo_Release_Open":
                return SRM;
            case "Confirm_Shipment_Closed":
            case "Shipment_Cancelled":
                return BOK;
            case "Equipment_VGM_Details_Updated":
                return VGM;
            case "Issue_Original_TPDOC_Closed":
            case "Issue_Verify_Copy_of_TPDOC_Closed":
            case "RELEASE":
                return TRD;
            case "Receive_Transport_Document_Instructions_Closed":
                return SHI;
            default:
                throw new MappingException("Could not map Shipment Information Type Code ".concat(eventAct));
        }
    }

}
