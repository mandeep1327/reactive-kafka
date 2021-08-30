package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.EquipmentEventType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import org.springframework.stereotype.Component;

import static MSK.com.external.dcsa.EquipmentEventType.DISC;
import static MSK.com.external.dcsa.EquipmentEventType.GTIN;
import static MSK.com.external.dcsa.EquipmentEventType.GTOT;
import static MSK.com.external.dcsa.EquipmentEventType.LOAD;
import static MSK.com.external.dcsa.EquipmentEventType.STRP;
import static MSK.com.external.dcsa.EquipmentEventType.STUF;
import static java.util.Objects.isNull;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ARRIVECUIMPN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.DEPARTCUEXPN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.DISCHARG_N;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.GATE_IN_EXPN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.GATE_OUTEXPY;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.LOAD_N;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.OFF_RAILIMPN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ON_RAIL_EXPN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.STRIPPIN_Y;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.STUFFINGEXPN;

@Component
public class EquipmentEventTypeMapper {

    public EquipmentEventType asEquipmentEventType(String eventAct) {
        if (isNull(eventAct)) {
            throw new MappingException("Null Event Act");
        }

        switch (eventAct) {
            case ARRIVECUIMPN:
            case GATE_IN_EXPN:
            case OFF_RAILIMPN:
                return GTIN;
            case DISCHARG_N:
                return DISC;
            case DEPARTCUEXPN:
            case GATE_OUTEXPY:
            case ON_RAIL_EXPN:
                return GTOT;
            case LOAD_N:
                return LOAD;
            case STRIPPIN_Y:
                return STRP;
            case STUFFINGEXPN:
                return STUF;
            default:
                throw new MappingException("Could not map Equipment Event Type of ".concat(eventAct));
        }
    }
}
