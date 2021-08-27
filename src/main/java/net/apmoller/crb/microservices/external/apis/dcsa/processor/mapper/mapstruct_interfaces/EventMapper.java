package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.gems.EquipmentType;
import MSK.com.gems.GTTSVesselType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.TransportPlanType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DCSAEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.EventClassifierCodeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ServiceTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EQUIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.TRANSPORT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstTransportPlanTypeWithPortOfLoad;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getLastTransportPlanWithPortOfDischarge;

@Mapper(componentModel = "spring",
        imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class, ServiceTypeMapper.class},
        uses = {DCSAEventTypeMapper.class, EventClassifierCodeMapper.class})
public interface EventMapper {

    @Mapping(target = "eventID", source = "event.eventId")
    @Mapping(target = "bookingReference", source = "shipment.bookNo")
    @Mapping(target = "eventDateTime", expression = "java(getDCSAEventDateTime(details))")
    @Mapping(target = "eventCreatedDateTime", source = "event.gemstsutc")
    @Mapping(target = "eventType", source = "details.event.eventAct")
    @Mapping(target = "eventClassifierCode", source = "details.event.eventAct")
    @Mapping(expression = "java(ReferenceMapper.getReferencesFromPubSetType(details))", target = "references")
    @Mapping(expression = "java(PartyMapper.getPartiesFromPubSetType(details))", target = "parties")
    @Mapping(expression = "java(EventUtility.getSourceSystemFromPubsetType(details))", target = "sourceSystem")
    @Mapping(expression = "java(EventUtility.getBolNumber(details))", target = "transportDocumentReference")
    @Mapping(expression = "java(EventUtility.getBookingNumber(details))", target = "carrierBookingReference")
    @Mapping(expression = "java(EventUtility.fromPubSetTypeToEquipmentReference(details))", target = "equipmentReference")
    @Mapping(expression = "java(EventUtility.fromPubSetTypeToCarrierCode(details))", target = "carrierCode")
    @Mapping(expression = "java(ServiceTypeMapper.getServiceTypeFromPubSetType(details))", target = "serviceType")
    Event fromPubSetTypeToEvent(PubSetType details);

    default String getDCSAEventDateTime(PubSetType pubSetType) {
        String eventAct = getEventAct(pubSetType);
        if (SHIPMENT_EVENTS.contains((eventAct))) {
            return pubSetType.getEvent().getGemstsutc();
        } else if (TRANSPORT_EVENTS.contains((eventAct))) {
            return getEventDateTimeForTransportEvents(pubSetType, eventAct);
        } else if (EQUIPMENT_EVENTS.contains(eventAct)) {
            return getEventDateTimeForEquipmentEvents(pubSetType);
        }
        throw new MappingException("Could not map eventType");
    }

    private String getEventDateTimeForTransportEvents(PubSetType pubSetType, String eventAct) {
        if (SHIPMENT_ETA.equals(eventAct) || SHIPMENT_ETD.equals(eventAct)){
            return getEventDateTimeForSpecialTransportEvents(pubSetType, eventAct);
        } else {
            return getEventDateTimeForOtherTransportEvents(pubSetType);
        }
    }

    private String getEventDateTimeForEquipmentEvents(PubSetType pubSetType) {
        final var moveType = Optional.ofNullable(pubSetType)
                .map(PubSetType::getEquipment)
                .filter(list -> !list.isEmpty())
                .map(equipmentTypes -> equipmentTypes.get(0))
                .map(EquipmentType::getMove)
                .orElseThrow(MappingException::new);
        var date = Optional.ofNullable(moveType.getActDte()).orElseThrow(MappingException::new);
        var time = Optional.ofNullable(moveType.getActTim()).orElseThrow(MappingException::new);
        return date.concat(time);
    }

    default String getEventDateTimeForSpecialTransportEvents(PubSetType pubSetType, String eventAct){
        if (SHIPMENT_ETA.equals(eventAct)) {
            return getLastTransportPlanWithPortOfDischarge(pubSetType)
                    .map(this::getPrioritizedTimestampForArrival)
                    .orElse(null);
        } else {
            return getFirstTransportPlanTypeWithPortOfLoad(pubSetType)
                    .map(this::getPrioritizedTimestampForDeparture)
                    .orElse(null);
        }
    }

    private String getEventDateTimeForOtherTransportEvents(PubSetType pubSetType) {
        var date = Optional.ofNullable(pubSetType)
                .map(PubSetType::getGttsvessel)
                .map(GTTSVesselType::getGttsdte)
                .orElse("");
        var time = Optional.ofNullable(pubSetType)
                .map(PubSetType::getGttsvessel)
                .map(GTTSVesselType::getGttstim)
                .orElse("");
        if (date.concat(time).isBlank()) {
            assert pubSetType != null;
            return pubSetType.getEvent().getGemstsutc();
        } else {
            return date.concat(time);
        }
    }

    private String getPrioritizedTimestampForArrival(TransportPlanType tp) {
        if (tp.getGttsexpArvTS() != null) {
            return tp.getGttsexpArvTS();
        } else {
            if (tp.getGcssexpArvTS() != null) {
                return tp.getGcssexpArvTS();
            } else {
                return tp.getGsisexpArvTS();
            }
        }
    }

    private String getPrioritizedTimestampForDeparture(TransportPlanType tp) {
        if (tp.getGttsexpDepTS() != null) {
            return tp.getGttsexpDepTS();
        } else {
            if (tp.getGcssexpDepTS() != null) {
                return tp.getGcssexpDepTS();
            } else {
                return tp.getGsisexpDepTS();
            }
        }
    }
}
