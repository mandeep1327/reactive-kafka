package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.FacilityType;
import MSK.com.external.dcsa.TransPortMode;
import MSK.com.external.dcsa.TransportCall;
import MSK.com.gems.EndLocType;
import MSK.com.gems.EquipmentType;
import MSK.com.gems.EventType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.StartLocType;
import MSK.com.gems.TransportPlanType;
import lombok.experimental.UtilityClass;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.MappingException;


import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static MSK.com.external.dcsa.FacilityType.CLOC;
import static MSK.com.external.dcsa.FacilityType.DEPO;
import static MSK.com.external.dcsa.FacilityType.INTE;
import static MSK.com.external.dcsa.FacilityType.POTE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getArrivalOrDepartureEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstEquipmentElement;

@UtilityClass
public final class TransportCallMapper {

    public static TransportCall fromPubsetToTransportCallBase(PubSetType pubSetType) {
        var event = pubSetType.getEvent();
        var eventAct = event.getEventAct();
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        return getTransportCall(eventAct, equipmentFirstElement);
    }

    private static TransportCall getTransportCall(String eventAct, EquipmentType equipmentFirstElement) {
        var transportCall = new TransportCall();
        transportCall.setFacilityType(getFacilityCodeType(eventAct));
        transportCall.setCarrierServiceCode(getCarrierServiceCode(equipmentFirstElement));
        transportCall.setOtherFacility(getLocation(equipmentFirstElement));
        return transportCall;
    }

    public static TransportCall fromPubsetToTransportCall(PubSetType pubSetType) {
        var base = fromPubsetToTransportCallBase(pubSetType);
        var transportPlanTypeList = pubSetType.getTransportPlan();
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        var transportPlan = Optional.ofNullable(getProperLocationFromTransportPlan(transportPlanTypeList, pubSetType.getEvent()))
                .map(e -> e.get(getActLocation(equipmentFirstElement))).orElse(null);
        return getFatterTransportCall(base, transportPlan);
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
                .orElseThrow(() -> new MappingException("Could not find the vessel code"));

    }

    private static String getLocation(EquipmentType equipmentTypeElement){
        return Optional.ofNullable(equipmentTypeElement.getMove().getActLoc())
                .orElseThrow(() -> new MappingException("Could not find the RKST code for the location"));
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
                throw new MappingException("Could not map TransportMode Code ".concat(transportModeCode));
        }
    }

    private static FacilityType getFacilityCodeType(String eventAct) {
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

    private static String getCarrierServiceCode(EquipmentType equipment) {
        if (!Objects.isNull(equipment.getMove()) &&
                !Objects.isNull(equipment.getMove().getLineCde())) {
            return equipment.getMove().getLineCde();
        }
        throw new MappingException("Could not Map the carrier service code");
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
