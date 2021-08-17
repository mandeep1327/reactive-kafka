package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import com.maersk.jaxb.pojo.EndLocType;
import com.maersk.jaxb.pojo.EquipmentType;
import com.maersk.jaxb.pojo.EventType;
import com.maersk.jaxb.pojo.MoveType;
import com.maersk.jaxb.pojo.PubSetType;
import com.maersk.jaxb.pojo.ShipmentType;
import com.maersk.jaxb.pojo.StartLocType;
import com.maersk.jaxb.pojo.TPDocType;
import com.maersk.jaxb.pojo.TransportPlanType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.CLOC;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.DEPO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.INTE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.POTE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getArrivalOrDepartureEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstEquipmentElement;

@Component
public final class TransportCallMapper {

    protected TransportCallMapper() {}

    public static TransportCall fromPubsetToTransportCallBase(PubSetType pubSetType) {
        var event = pubSetType.getEvent();
        var eventAct = event.getEventAct().toString();
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        return TransportCall.builder()
                .facilityTypeCode(getFacilityCodeType(eventAct))
                .carrierServiceCode(getCarrierServiceCode(equipmentFirstElement))
                .otherFacility(getLocation(equipmentFirstElement))
                .build();
    }

    public static TransportCall fromPubsetToTransportCall(PubSetType pubSetType) {
        var base = fromPubsetToTransportCallBase(pubSetType);
        var transportPlanTypeList = pubSetType.getTransportPlan();
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        var transportPlan = Optional.ofNullable(getProperLocationFromTransportPlan(transportPlanTypeList, pubSetType.getEvent()))
                .map(e -> e.get(getActLocation(equipmentFirstElement))).orElse(null);
        return TransportCall.builder()
                .facilityTypeCode(base.getFacilityTypeCode())
                .carrierServiceCode(base.getCarrierServiceCode())
                .carrierVoyageNumber(getVoyageNumberFromTransportPlan(transportPlan))
                .modeOfTransport(getModeOfTransport(transportPlan))
                .otherFacility(base.getOtherFacility())
                .build();
    }

    protected static String getVoyageNumberFromTransportPlan(TransportPlanType transportPlanMap) {
        return Optional.ofNullable(transportPlanMap)
                .map(TransportPlanType::getVesselCde)
                .map(CharSequence::toString)
                .orElseThrow(() -> new MappingException("Could not find the vessel code"));

    }

    protected static String getLocation(EquipmentType equipmentTypeElement){
        return Optional.ofNullable(equipmentTypeElement.getMove().getActLoc())
                .map(CharSequence::toString)
                .orElseThrow(() -> new MappingException("Could not find the RKST code for the location"));
    }


    protected static TransportCall.TransPortMode getModeOfTransport(TransportPlanType transportPlanType) {
        var transportModeCode = Optional.ofNullable(transportPlanType)
                .map(TransportPlanType::getTransMode)
                .map(CharSequence::toString)
                .orElse("DEFAULT");
        switch (transportModeCode.toUpperCase()) {
            case "BAR":
            case "BCO":
                return TransportCall.TransPortMode.BARGE;
            case "MVS":
            case "FEO":
            case "FEF":
            case "VSL":
            case "VSF":
            case "VSM":
                return TransportCall.TransPortMode.VESSEL;
            case "TRK":
                return TransportCall.TransPortMode.TRUCK;
            case "RR":
            case "RCO":
                return TransportCall.TransPortMode.RAIL;
            default:
                throw new MappingException("Could not map TransportMode Code ".concat(transportModeCode));
        }
    }

    protected static TransportCall.FacilityTypeCode getFacilityCodeType(String eventAct) {
        switch (eventAct) {
            case "ARRIVECUIMPN":
            case "DEPARTCUEXPN":
                return CLOC;
            case "CONTAINER ARRIVAL":
            case "CONTAINER DEPARTURE":
            case "Shipment_ETA":
            case "Shipment_ETD":
            case "GATE-IN EXPN":
            case "LOAD       N":
            case "STRIPPIN   Y":
            case "STUFFINGEXPN":
                return POTE;
            case "RAIL_ARRIVAL_AT_DESTINATION":
            case "RAIL_DEPARTURE":
            case "OFF-RAILIMPN":
            case "ON-RAIL EXPN":
                return INTE;
            case "GATE-OUTEXPY":
                return DEPO;
            default:
                throw new MappingException("Could not map Facility Code Type ".concat(eventAct));
        }
    }

    protected static String getCarrierServiceCode(EquipmentType equipment) {
        if (!Objects.isNull(equipment.getMove()) &&
                !Objects.isNull(equipment.getMove().getLineCde())) {
            return equipment.getMove().getLineCde().toString();
        }
        throw new MappingException("Could not Map the carrier service code");
    }

    protected static String getActLocation(EquipmentType firstEquipmentType) {
        if (!Objects.isNull(firstEquipmentType.getMove()) &&
                !Objects.isNull(firstEquipmentType.getMove().getActLoc())) {
                return firstEquipmentType.getMove().getActLoc().toString();
        }
        return null;
    }

    protected static Map<String, TransportPlanType> getProperLocationFromTransportPlan(List<TransportPlanType> transportPlan, EventType eventType) {
        var typeOfTransport = getArrivalOrDepartureEventType(eventType.getEventAct().toString());
        var transportPlans = Optional.ofNullable(transportPlan)
                .filter(transportPlanTypes -> !transportPlanTypes.isEmpty())
                .orElseThrow(() -> new MappingException("TransportPlan can not be empty for the Transport Event"));
        switch (typeOfTransport){
            case ARRI:
                return transportPlans.stream()
                        .collect(Collectors.toMap(e -> getEndLocation(e.getEndLoc()), Function.identity()));
            case DEPA:
                return transportPlans.stream()
                        .collect(Collectors.toMap(e -> getStartLocation(e.getStartLoc()), Function.identity()));
            default:
                return null;

        }
    }

    protected static String getEndLocation (EndLocType endLocType){
        if (!Objects.isNull(endLocType)){
            return endLocType.getValue().toString();
        }
        return null;
    }

    protected static String getStartLocation (StartLocType startLocType){
        if (!Objects.isNull(startLocType)){
            return startLocType.getValue().toString();
        }
        return null;
    }

}
