package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.external.dcsa.EventClassifierCode;
import MSK.com.gems.EquipmentType;
import MSK.com.gems.EventType;
import MSK.com.gems.GTTSVesselType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.TransportPlanType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DCSAEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ServiceTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

import static MSK.com.external.dcsa.EventClassifierCode.ACT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ACT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EQUIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EST_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.TRANSPORT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstTransportPlanTypeWithPortOfLoad;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getLastTransportPlanWithPortOfDischarge;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getTimeStampInEpochMillis;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getTimeStampInEpochMillisFromDateAndTime;

@Mapper(componentModel = "spring",
        imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class, ServiceTypeMapper.class},
        uses = {DCSAEventTypeMapper.class})
public interface EventMapper {

    @Mapping(target = "eventID", source = "event.eventId")
    @Mapping(target = "bookingReference", source = "shipment.bookNo")
    @Mapping(target = "eventDateTime", expression = "java(getDCSAEventDateTime(details))")
    @Mapping(target = "eventCreatedDateTime", expression = "java(getUTCFromNonFormattedTimestamp(details))")
    @Mapping(target = "eventType", source = "details.event.eventAct")
    @Mapping(target = "eventClassifierCode", expression = "java(getClassifierCode(details))")
    @Mapping(expression = "java(ReferenceMapper.getReferencesFromPubSetType(details))", target = "references")
    @Mapping(expression = "java(PartyMapper.getPartiesFromPubSetType(details))", target = "parties")
    @Mapping(expression = "java(EventUtility.getSourceSystemFromPubsetType(details))", target = "sourceSystem")
    @Mapping(expression = "java(EventUtility.getBolNumber(details))", target = "transportDocumentReference")
    @Mapping(expression = "java(EventUtility.getBookingNumber(details))", target = "carrierBookingReference")
    @Mapping(expression = "java(EventUtility.fromPubSetTypeToEquipmentReference(details))", target = "equipmentReference")
    @Mapping(expression = "java(EventUtility.fromPubSetTypeToCarrierCode(details))", target = "carrierCode")
    @Mapping(expression = "java(ServiceTypeMapper.getServiceTypeFromPubSetType(details))", target = "serviceType")
    Event fromPubSetTypeToEvent(PubSetType details);

    default Long getDCSAEventDateTime(PubSetType pubSetType) {
        String eventAct = getEventAct(pubSetType);
        if (SHIPMENT_EVENTS.contains((eventAct))) {
            return getIsoFormatTimestamp(pubSetType);
        } else if (TRANSPORT_EVENTS.contains((eventAct))) {
            return getEventDateTimeForTransportEvents(pubSetType, eventAct);
        } else if (EQUIPMENT_EVENTS.contains(eventAct)) {
            return getEventDateTimeForEquipmentEvents(pubSetType);
        }
        throw new MappingException("Could not map eventType");
    }

    default Long getUTCFromNonFormattedTimestamp(PubSetType pubSetType){
        return getGemsUTCTimestamp(pubSetType)
                .map(EventUtility::getTimeStampInEpochMillis)
                .orElse(null);
    }

    default Long getIsoFormatTimestamp(PubSetType pubSetType){
        return getGemsUTCTimestamp(pubSetType)
                .map(EventUtility::getTimeStampInEpochMillis)
                .orElse(null);
    }

    @NotNull
    private Optional<String> getGemsUTCTimestamp(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType)
                .map(PubSetType::getEvent)
                .map(EventType::getGemstsutc);
    }

    default EventClassifierCode getClassifierCode(PubSetType pubSetType) {
        var act = getEventAct(pubSetType);
        if (EST_EVENTS.contains(act)) {
            return getEventClassifierCodeForESTEvents(pubSetType, act);
        } else if (ACT_EVENTS.contains(act)) {
            return ACT;
        }
        throw new MappingException("Could not map EventClassifierCode");
    }

    private EventClassifierCode getEventClassifierCodeForESTEvents(PubSetType pubSetType, String act) {
        if (SHIPMENT_ETA.equals(act)) {
            return getLastTransportPlanWithPortOfDischarge(pubSetType)
                    .map(TransportPlanType::getGttsactArvTS)
                    .isPresent() ? ACT : EventClassifierCode.EST;
        } else {
            return getFirstTransportPlanTypeWithPortOfLoad(pubSetType)
                    .map(TransportPlanType::getGttsactDepTS)
                    .isPresent() ? ACT : EventClassifierCode.EST;
        }
    }

    private Long getEventDateTimeForTransportEvents(PubSetType pubSetType, String eventAct) {
        String eventDateTime;
        if (SHIPMENT_ETA.equals(eventAct) || SHIPMENT_ETD.equals(eventAct)) {
            eventDateTime = getEventDateTimeForSpecialTransportEvents(pubSetType, eventAct);
        } else {
            eventDateTime = getEventDateTimeForOtherTransportEvents(pubSetType);
        }
        return getTimeStampInEpochMillis(eventDateTime);
    }

    private Long getEventDateTimeForEquipmentEvents(PubSetType pubSetType) {
        final var moveType = Optional.ofNullable(pubSetType)
                .map(PubSetType::getEquipment)
                .filter(list -> !list.isEmpty())
                .map(equipmentTypes -> equipmentTypes.get(0))
                .map(EquipmentType::getMove)
                .orElseThrow(MappingException::new);
        var date = Optional.ofNullable(moveType.getActDte()).orElseThrow(MappingException::new);
        var time = Optional.ofNullable(moveType.getActTim()).orElseThrow(MappingException::new);
        return getTimeStampInEpochMillisFromDateAndTime(date, time);
    }

    default String getEventDateTimeForSpecialTransportEvents(PubSetType pubSetType, String eventAct) {
        var eventClassifierCode = getEventClassifierCodeForESTEvents(pubSetType, eventAct);
        var lastPortOfDischarge = getLastTransportPlanWithPortOfDischarge(pubSetType);
        var firstPortOfLoad = getFirstTransportPlanTypeWithPortOfLoad(pubSetType);
        switch (eventClassifierCode) {
            case ACT:
                return getActualEventDateTime(eventAct, lastPortOfDischarge, firstPortOfLoad);
            case EST:
                return getEstimatedEventDateTime(eventAct, lastPortOfDischarge, firstPortOfLoad);
            default:
                return null;
        }
    }

    private String getEstimatedEventDateTime(String eventAct, Optional<TransportPlanType> lastPOD, Optional<TransportPlanType> firstPOL) {
        return SHIPMENT_ETA
                .equals(eventAct) ? lastPOD.map(this::getPrioritizedTimestampForArrival).orElse(null)
                : firstPOL.map(this::getPrioritizedTimestampForDeparture).orElse(null);
    }

    private String getActualEventDateTime(String eventAct, Optional<TransportPlanType> lastPOD, Optional<TransportPlanType> firstPOL) {
        return SHIPMENT_ETA
                .equals(eventAct) ? lastPOD.map(TransportPlanType::getGttsactArvTS).orElse(null)
                : firstPOL.map(TransportPlanType::getGttsactDepTS).orElse(null);
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
        } else if (tp.getGcssexpArvTS() != null) {
            return tp.getGcssexpArvTS();
        }
        return null;
    }

    private String getPrioritizedTimestampForDeparture(TransportPlanType tp) {
        if (tp.getGttsexpDepTS() != null) {
            return tp.getGttsexpDepTS();
        } else if (tp.getGcssexpDepTS() != null) {
            return tp.getGcssexpDepTS();
        }
        return null;
    }
}
