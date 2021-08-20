package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.external.dcsa.EventClassifierCode;
import MSK.com.external.dcsa.EventType;
import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ServiceTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.mapping.MappingException;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ACT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EQUIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EST_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.TRANSPORT_EVENTS;

@Mapper(componentModel = "spring",
        imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class, ServiceTypeMapper.class})
public interface EventMapper {

    @Mapping(target = "eventID", source = "event.eventId")
    @Mapping(target = "bookingReference", source = "shipment.bookNo")
    @Mapping(target = "eventDateTime", expression = "java(getDCSAEventDateTime(details))")
    @Mapping(target = "eventCreatedDateTime", source = "event.gemstsutc")
    @Mapping(target = "eventType", expression = "java(getDCSAEventType(details.getEvent().getEventAct()))")
    @Mapping(target = "eventClassifierCode", expression = "java(getEventClassifierCode(details.getEvent().getEventAct()))")
    @Mapping(expression = "java(ReferenceMapper.getReferencesFromPubSetType(details))", target = "references")
    @Mapping(expression = "java(PartyMapper.getPartiesFromPubSetType(details))", target = "parties")
    @Mapping(expression = "java(EventUtility.getSourceSystemFromPubsetType(details))", target = "sourceSystem")
    @Mapping(expression = "java(EventUtility.getBolNumber(details))", target = "transportDocumentReference")
    @Mapping(expression = "java(EventUtility.getBookingNumber(details))", target = "carrierBookingReference")
    @Mapping(expression = "java(EventUtility.fromPubSetTypeToEquipmentReference(details))", target = "equipmentReference")
    @Mapping(expression = "java(EventUtility.fromPubSetTypeToCarrierCode(details))", target = "carrierCode")
    @Mapping(expression = "java(ServiceTypeMapper.getServiceTypeFromPubSetType(details))", target = "serviceType")
    Event fromPubSetTypeToEvent(PubSetType details);

    default EventType getDCSAEventType(String act) {
        if (SHIPMENT_EVENTS.contains(act)) {
            return EventType.SHIPMENT;
        } else if (TRANSPORT_EVENTS.contains(act)) {
            return EventType.TRANSPORT;
        } else if (EQUIPMENT_EVENTS.contains(act)) {
            return EventType.EQUIPMENT;
        }
        throw new MappingException("Could not map eventType");
    }

    default String getDCSAEventDateTime(PubSetType pubSetType) {
        //This is something we need to format the timestamp
        String eventAct = pubSetType.getEvent().getEventAct();
        if (SHIPMENT_EVENTS.contains((eventAct))) {
            return pubSetType.getEvent().getGemstsutc();
        } else if (TRANSPORT_EVENTS.contains((eventAct))) {
            var date = pubSetType.getGttsvessel().getGttsdte();
            var time = pubSetType.getGttsvessel().getGttstim();
            return date.concat(time);
        } else if (EQUIPMENT_EVENTS.contains(eventAct)) {
            final var equipmentType = pubSetType.getEquipment().get(0);
            var date = equipmentType.getMove().getActDte();
            var time = equipmentType.getMove().getActTim();
            return date.concat(time);
        }
        throw new MappingException("Could not map eventType");
    }


    default EventClassifierCode getEventClassifierCode(String act) {
        if(EST_EVENTS.contains(act)) {
            return EventClassifierCode.EST;
        } else if (ACT_EVENTS.contains(act)) {
            return EventClassifierCode.ACT;
        }
        throw new MappingException("Could not map EventClassifierCode");
    }

}
