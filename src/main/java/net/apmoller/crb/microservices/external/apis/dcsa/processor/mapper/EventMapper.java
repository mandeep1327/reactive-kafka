package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import com.maersk.jaxb.pojo.EventType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mapping.MappingException;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    private final List<String> SHIPMENT_EVENTS = Arrays.asList(
            "Confirm_Shipment_Closed",
            "Shipment-Cancelled",
            "Receive_Transport_Document_Instructions_Closed",
            "Equipment_VGM_Details_Updated",
            "Arrange_Cargo_Release_Closed",
            "Arrange_Cargo_Release_Open",
            "Issue_Original_TPDOC_Closed",
            "Issue_Verify_Copy_of_TPDOC_Closed");

    private final List<String> TRANSPORT_EVENTS = Arrays.asList(
            "CONTAINER_ARRIVAL",
            "ETA_Changed_After_final_water_leg",
            "DROPCUST",
            "ARRIVECU",
            "RAIL_ARRIVAL_AT_DESTINATION",
            "DEPARTCU",
            "PICKCUST",
            "RAIL_DEPARTURE",
            "SHIPMENT_ETA",
            "SHIPMENT_ETD");

    private final List<String> EQUIPMENT_EVENTS = Arrays.asList(
            "LOAD",
            "DISCHARG",
            "GATE-IN",
            "GATE-OUT",
            "DEPARTCU",
            "ON-RAIL",
            "OFF-RAIL",
            "STUFFING",
            "STRIPPIN");

    private final List<String> EST_EVENTS = Arrays.asList(
            "SHIPMENT_ETA",
            "SHIPMENT_ETD");

    private final List<String> ACT_EVENTS = Arrays.asList(
            "RAIL_ARRIVAL_AT_DESTINATION",
            "ARRIVECUIMPN",
            "DEPARTCUEXPN",
            "RAIL_DEPARTURE",
            "Arrange_Cargo_Release_Closed",
            "Confirm_Shipment_Closed",
            "Shipment_Cancelled",
            "Receive_Transport_Document_Instructions_Closed",
            "Equipment_VGM_Details_Updated",
            "Issue_Original_TPDOC_Closed",
            "Issue_Verify_Copy_of_TPDOC_Closed",
            "RELEASE",
            "Arrange_Cargo_Release_Open",
            "ARRIVAL_NOTICE",  //This is eDOCEvent.notificationType ?
            "DISCHARG   N",
            "GATE-OUTEXPY",
            "LOAD       N",
            "GATE-IN EXPN",
            "OFF-RAILIMPN",
            "ON-RAIL EXPN",
            "ARRIVECUIMPN",
            "DEPARTCUEXPN",
            "STUFFINGEXPN",
            "STRIPPIN   Y",
            "CONTAINER ARRIVAL",
            "CONTAINER DEPARTURE"
    );

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "startLocation.maerskCityGeoId", source = "fromLocation.cityGeoId")
    @Mapping(target = "startLocation.maerskSiteGeoId", source = "fromLocation.siteGeoId")
    @Mapping(target = "endLocation.maerskCityGeoId", source = "toLocation.cityGeoId")


    @Mapping(target = "eventType", source = "java(getDCSAEventType(eventAct))")
    @Mapping(target = "eventCreatedDateTime", source = "transport.vessel.code")
    @Mapping(target = "eventClassifierCode", source = "java(getEventClassifierCode(eventAct))")
    abstract Event fromEventTypeToEvent(EventType details);

    Event.EventType getDCSAEventType(String act) {
        if (SHIPMENT_EVENTS.contains(act)) {
            return Event.EventType.SHIPMENT;
        } else if (TRANSPORT_EVENTS.contains(act)) {
            return Event.EventType.TRANSPORT;
        } else if (EQUIPMENT_EVENTS.contains(act)) {
            return Event.EventType.EQUIPMENT;
        }
        throw new MappingException("Could not map eventType");
    }

    Event.EventClassifierCode getEventClassifierCode(String act) {
        if(EST_EVENTS.contains(act)) {
            return Event.EventClassifierCode.EST;
        } else if (ACT_EVENTS.contains(act)) {
            return Event.EventClassifierCode.ACT;
        }
        throw new MappingException("Could not map EventClassifierCode");
    }

}
