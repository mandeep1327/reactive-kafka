package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.EquipmentEventType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static MSK.com.external.dcsa.EquipmentEventType.DISC;
import static MSK.com.external.dcsa.EquipmentEventType.GTIN;
import static MSK.com.external.dcsa.EquipmentEventType.GTOT;
import static MSK.com.external.dcsa.EquipmentEventType.LOAD;
import static MSK.com.external.dcsa.EquipmentEventType.STRP;
import static MSK.com.external.dcsa.EquipmentEventType.STUF;
import static java.util.Objects.nonNull;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ARRIVECU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.DEPARTCU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.DISCHARG;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.GATE_IN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.GATE_OUT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.OFF_RAIL;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ON_RAIL;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.STRIPPIN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.STUFFING;

@Component
public class EquipmentEventTypeMapper {

    public EquipmentEventType asEquipmentEventType(String eventAct) {
        if (nonNull(eventAct)) {
            if (isSTUF(eventAct)) {
                return STUF;
            } else if (isSTRP(eventAct)) {
                return STRP;
            } else if (isLOAD(eventAct)) {
                return LOAD;
            } else if (isDISC(eventAct)) {
                return DISC;
            } else if (isGTIN(eventAct)) {
                return GTIN;
            } else if (isGTOT(eventAct)) {
                return GTOT;
            }
        }
        throw new MappingException("Could not map Equipment Event Type of ".concat(eventAct));
    }

    private static boolean isSTUF(String eventAct) {
        return eventAct.startsWith(STUFFING);
    }

    private static boolean isSTRP(String eventAct) {
        return eventAct.startsWith(STRIPPIN);
    }

    private static boolean isLOAD(String eventAct) {
        return eventAct.startsWith(EventUtility.LOAD);
    }

    private static boolean isDISC(String eventAct) {
        return eventAct.startsWith(DISCHARG);
    }

    private static boolean isGTIN(String eventAct) {
        return Stream.of(ARRIVECU, GATE_IN, OFF_RAIL).anyMatch(eventAct::startsWith);
    }

    private static boolean isGTOT(String eventAct) {
        return Stream.of(DEPARTCU, GATE_OUT, ON_RAIL).anyMatch(eventAct::startsWith);
    }
}
