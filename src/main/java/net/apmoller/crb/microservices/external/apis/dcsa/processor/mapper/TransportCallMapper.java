package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.TransPortMode;
import MSK.com.external.dcsa.TransportCall;
import MSK.com.gems.EndLocType;
import MSK.com.gems.EquipmentType;
import MSK.com.gems.EventType;
import MSK.com.gems.MoveType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.StartLocType;
import MSK.com.gems.TransportPlanType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.MappingException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getArrivalOrDepartureEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstEquipmentElement;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstTransportPlanTypeWithPortOfLoad;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getLastTransportPlanWithPortOfDischarge;

@UtilityClass
@Slf4j
public final class TransportCallMapper {

    public static TransportCall fromPubsetToTransportCallBase(PubSetType pubSetType) {
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        return getTransportCall(equipmentFirstElement);
    }

    private static TransportCall getTransportCall( EquipmentType equipmentFirstElement) {
        var transportCall = new TransportCall();
        transportCall.setCarrierServiceCode(getCarrierServiceCode(equipmentFirstElement));
        transportCall.setOtherFacility(getLocation(equipmentFirstElement));
        return transportCall;
    }

    public static TransportCall fromPubsetToTransportCall(PubSetType pubSetType) {
        var base = fromPubsetToTransportCallBase(pubSetType);
        var transportPlanTypeList = pubSetType.getTransportPlan();
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        var transportPlan = getTransportPlan(pubSetType, transportPlanTypeList, equipmentFirstElement);
        return getFatterTransportCall(base, transportPlan);
    }

    private TransportPlanType getTransportPlan(PubSetType pubSetType, List<TransportPlanType> transportPlanTypeList, EquipmentType equipmentFirstElement) {
        var eventAct = getEventAct(pubSetType);
        if (isETAorETDEvent(eventAct)){
            if (SHIPMENT_ETA.equals(eventAct)) {
                return getLastTransportPlanWithPortOfDischarge(pubSetType).orElse(null);
            } else {
                return getFirstTransportPlanTypeWithPortOfLoad(pubSetType).orElse(null);
            }
        } else {
            return getTransportPlanTypeForOtherEvents(pubSetType, transportPlanTypeList, equipmentFirstElement);
        }
    }

    private boolean isETAorETDEvent(String eventAct) {
        return SHIPMENT_ETA.equals(eventAct) || SHIPMENT_ETD.equals(eventAct);
    }


    private TransportPlanType getTransportPlanTypeForOtherEvents(PubSetType pubSetType, List<TransportPlanType> transportPlanTypeList, EquipmentType equipmentFirstElement) {
        return Optional.ofNullable(getProperLocationFromTransportPlan(transportPlanTypeList, pubSetType.getEvent()))
                .map(e -> e.get(getActLocation(equipmentFirstElement))).orElse(null);
    }

    private static TransportCall getFatterTransportCall(TransportCall base, TransportPlanType transportPlan) {

        var transportCall = new TransportCall();

        transportCall.setFacilityType(base.getFacilityType());
        transportCall.setCarrierServiceCode(base.getCarrierServiceCode());
        transportCall.setCarrierVoyageNumber(getVoyageNumberFromTransportPlan(transportPlan));
        transportCall.setModeOfTransport(getModeOfTransport(transportPlan));
        transportCall.setOtherFacility(base.getOtherFacility());
        return transportCall;
    }

    private static String getVoyageNumberFromTransportPlan(TransportPlanType transportPlanMap) {
        return Optional.ofNullable(transportPlanMap)
                .map(TransportPlanType::getVesselCde)
                .orElse(null);

    }

    private static String getLocation(EquipmentType equipmentTypeElement){
        return Optional.ofNullable(equipmentTypeElement.getMove())
                .map(MoveType::getActLoc)
                .orElse(null);
    }


    private static TransPortMode getModeOfTransport(TransportPlanType transportPlanType) {
        var transportModeCode = Optional.ofNullable(transportPlanType)
                .map(TransportPlanType::getTransMode)
                .orElse("DEFAULT");
        switch (transportModeCode.toUpperCase()) {
            case "BAR":
            case "BCO":
                return TransPortMode.BARGE;
            case "MVS":
            case "FEO":
            case "FEF":
            case "VSL":
            case "VSF":
            case "VSM":
                return TransPortMode.VESSEL;
            case "TRK":
                return TransPortMode.TRUCK;
            case "RR":
            case "RCO":
                return TransPortMode.RAIL;
            default:
                throw new MappingException("TransportPlan can not be empty for the Transport Event");
        }
    }


    private static String getCarrierServiceCode(EquipmentType equipment) {
        if (!Objects.isNull(equipment.getMove()) &&
                !Objects.isNull(equipment.getMove().getLineCde())) {
            return equipment.getMove().getLineCde();
        }
        log.error("Could not Map the carrier service code");
        return null;
    }

    private static String getActLocation(EquipmentType firstEquipmentType) {
        if (!Objects.isNull(firstEquipmentType.getMove()) &&
                !Objects.isNull(firstEquipmentType.getMove().getActLoc())) {
                return firstEquipmentType.getMove().getActLoc();
        }
        return null;
    }

    private static Map<String, TransportPlanType> getProperLocationFromTransportPlan(List<TransportPlanType> transportPlan, EventType eventType) {
        var typeOfTransport = getArrivalOrDepartureEventType(eventType.getEventAct());
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

    private static String getEndLocation (EndLocType endLocType){
        if (!Objects.isNull(endLocType)){
            return (endLocType.getValue());
        }
        return null;
    }

    private static String getStartLocation (StartLocType startLocType){
        if (!Objects.isNull(startLocType)){
            return (startLocType.getValue());
        }
        return null;
    }

}
