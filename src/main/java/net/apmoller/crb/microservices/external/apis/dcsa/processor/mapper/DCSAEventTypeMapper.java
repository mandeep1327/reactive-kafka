package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.EventType;
import org.springframework.stereotype.Component;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EQUIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.TRANSPORT_EVENTS;

@Component
public class DCSAEventTypeMapper {

    public EventType asDCSAEventType(String act) {
        if (SHIPMENT_EVENTS.contains(act)) {
            return EventType.SHIPMENT;
        } else if (TRANSPORT_EVENTS.contains(act)) {
            return EventType.TRANSPORT;
        } else if (EQUIPMENT_EVENTS.contains(act)) {
            return EventType.EQUIPMENT;
        }
        return null;
    }
}
