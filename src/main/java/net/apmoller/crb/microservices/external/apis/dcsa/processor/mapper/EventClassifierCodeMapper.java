package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.EventClassifierCode;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import org.springframework.stereotype.Component;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ACT_EVENTS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EST_EVENTS;

@Component
public class EventClassifierCodeMapper {

    public EventClassifierCode asEventClassifierCode(String act) {
        if(EST_EVENTS.contains(act)) {
            return EventClassifierCode.EST;
        } else if (ACT_EVENTS.contains(act)) {
            return EventClassifierCode.ACT;
        }
        throw new MappingException("Could not map EventClassifierCode");
    }
}
