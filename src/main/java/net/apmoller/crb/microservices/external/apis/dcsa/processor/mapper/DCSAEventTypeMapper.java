package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.EventType;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.TRANSPORT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.isEquipmentEvent;

@Component
public class DCSAEventTypeMapper {
    public EventType asDCSAEventType(String act) {
        if(nonNull(act)) {
            if (SHIPMENT_EVENTS.contains(act)) {
                return EventType.SHIPMENT;
            } else if (TRANSPORT_EVENTS.contains(act)) {
                return EventType.TRANSPORT;
            } else if (isEquipmentEvent(act)) {
                return EventType.EQUIPMENT;
            }
        }
        return null;
    }
}
