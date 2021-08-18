package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.external.dcsa.ShipmentEvent;
import MSK.com.external.dcsa.ShipmentEventType;
import MSK.com.external.dcsa.ShipmentInformationType;
import com.maersk.jaxb.pojo.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mapping.MappingException;

import static MSK.com.external.dcsa.ShipmentEventType.CONF;
import static MSK.com.external.dcsa.ShipmentEventType.DRFT;
import static MSK.com.external.dcsa.ShipmentEventType.ISSU;
import static MSK.com.external.dcsa.ShipmentEventType.PENA;
import static MSK.com.external.dcsa.ShipmentEventType.RECE;
import static MSK.com.external.dcsa.ShipmentEventType.REJE;
import static MSK.com.external.dcsa.ShipmentEventType.SURR;
import static MSK.com.external.dcsa.ShipmentInformationType.ARN;
import static MSK.com.external.dcsa.ShipmentInformationType.BOK;
import static MSK.com.external.dcsa.ShipmentInformationType.SHI;
import static MSK.com.external.dcsa.ShipmentInformationType.SRM;
import static MSK.com.external.dcsa.ShipmentInformationType.TRD;
import static java.util.Objects.isNull;

@Mapper(componentModel = "spring", imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class})
public interface ShipmentEventMapper {
    @Mapping(expression = "java(getShipmentEventType(pubSetType.getEvent().getEventAct().toString()))", target = "shipmentEventType")
    @Mapping(expression = "java(getShipmentInformationTypeCode(pubSetType.getEvent().getEventAct().toString()))", target = "shipmentInformationType")
    @Mapping(expression = "java(getDocumentId(pubSetType))", target = "documentID")
    @Mapping(source = "baseData.eventID", target = "eventID")
    @Mapping(source = "baseData.bookingReference", target = "bookingReference")
    @Mapping(source = "baseData.eventDateTime", target = "eventDateTime")
    @Mapping(source = "baseData.eventType", target = "eventType")
    @Mapping(source = "baseData.eventCreatedDateTime", target = "eventCreatedDateTime")
    @Mapping(source = "baseData.eventClassifierCode", target = "eventClassifierCode")
    @Mapping(source = "baseData.parties", target = "parties")
    @Mapping(source = "baseData.references", target = "references")
    @Mapping(source = "baseData.equipmentReference", target = "equipmentReference")
    @Mapping(source = "baseData.carrierBookingReference", target = "carrierBookingReference")
    @Mapping(source = "baseData.transportDocumentReference", target = "transportDocumentReference")
    @Mapping(source = "baseData.sourceSystem", target = "sourceSystem")
    @Mapping(source = "baseData.serviceType", target = "serviceType")
    @Mapping(source = "baseData.carrierCode", target = "carrierCode")
    ShipmentEvent fromPubSetTypeToShipmentEvent(PubSetType pubSetType, Event baseData);

    default String getDocumentId(PubSetType pubSetType) {
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
            case "ARRIVAL_NOTICE":
                return getDocumentIdForOthers(pubSetType);
            default:
                throw new MappingException("Booking Number is not available for Document ID");
        }
    }

    @NotNull
    private String getDocumentIdForOthers(PubSetType pubSetType) {
        if (!isNull(pubSetType.getTpdoc()) && !isNull(pubSetType.getTpdoc().get(0).getBolNo())) {
            return pubSetType.getTpdoc().get(0).getBolNo().toString();
        }
        throw new MappingException("Booking Number is not available for Document ID");
    }

    @NotNull
    private String getDocumentIdForBookingEvents(PubSetType pubSetType) {
        if (!isNull(pubSetType.getShipment()) && !isNull(pubSetType.getShipment().getBookNo())) {
            return pubSetType.getShipment().getBookNo().toString();
        }
        throw new MappingException("Booking Number is not available for Document ID");
    }

    default ShipmentEventType getShipmentEventType(String eventAct) {

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

    default ShipmentInformationType getShipmentInformationTypeCode(String eventAct) {

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
