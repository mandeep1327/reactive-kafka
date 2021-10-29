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
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static MSK.com.external.dcsa.TransportEventType.ARRI;
import static MSK.com.external.dcsa.TransportEventType.DEPA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ARRIVECU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.CONTAINER_ARRIVAL;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.CONTAINER_DEPARTURE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.DEPARTCU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.DISCHARG;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.GATE_IN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.GATE_OUT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.LOAD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.OFF_RAIL;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.ON_RAIL;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.RAIL_ARRIVAL_AT_DESTINATION;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.RAIL_DEPARTURE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_ETD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.STRIPPIN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.STUFFING;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getArrivalOrDepartureEquipmentEvent;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getArrivalOrDepartureEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstEquipmentElement;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstTransportPlanTypeWithPortOfLoad;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getLastTransportPlanWithPortOfDischarge;

@UtilityClass
@Slf4j
public final class TransportCallMapper {

    private static final String TRANSPORT_PLAN_EMPTY = "TransportPlan can not be empty for the Transport Event";

    public static TransportCall getTransportCallForTransportEvents(PubSetType pubSetType) {
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        var transportPlan = getTransportPlanForTransportEvent(pubSetType, equipmentFirstElement);
        var eventAct = getEventAct(pubSetType);
        return createTransportCall(transportPlan, equipmentFirstElement, eventAct, pubSetType.getTransportPlan());
    }

    public static String getVesselCode(PubSetType pubSetData) {
        var equipmentFirstElement = getFirstEquipmentElement(pubSetData);
        var transportPlan = getTransportPlanForTransportEvent(pubSetData, equipmentFirstElement);
        return getVesselCodeFromTransportPlan(transportPlan);

    }

    public static TransportCall getTransportCallForEquipmentEvents(PubSetType pubSetType) {
        var equipmentFirstElement = getFirstEquipmentElement(pubSetType);
        var transportPlan = getTransportPlanTypeForCommonEvents(pubSetType, equipmentFirstElement, false);
        var eventAct = getEventAct(pubSetType);

        return createTransportCall(transportPlan, equipmentFirstElement, eventAct, pubSetType.getTransportPlan());
    }

    private TransportPlanType getTransportPlanForTransportEvent(PubSetType pubSetType, EquipmentType equipmentFirstElement) {
        var eventAct = getEventAct(pubSetType);
        if (isETAorETDEvent(eventAct)) {
            return getTransportPlanTypeForShipmentEtaOrEtd(pubSetType, eventAct);
        }
        return getTransportPlanTypeForCommonEvents(pubSetType, equipmentFirstElement, true);
    }

    private TransportPlanType getTransportPlanTypeForShipmentEtaOrEtd(PubSetType pubSetType, String eventAct) {
        if (SHIPMENT_ETA.equals(eventAct)) {
            return getLastTransportPlanWithPortOfDischarge(pubSetType).orElseThrow(() -> new MappingException(TRANSPORT_PLAN_EMPTY));
        }
        return getFirstTransportPlanTypeWithPortOfLoad(pubSetType).orElseThrow(() -> new MappingException(TRANSPORT_PLAN_EMPTY));
    }


    private TransportPlanType getTransportPlanTypeForCommonEvents(PubSetType pubSetType, EquipmentType equipmentFirstElement, boolean isTransportEvent) {
        var transportPlanTypeList = pubSetType.getTransportPlan();
        var mapOfTransportPlan = isTransportEvent
                ? fetchForTransportEvents(pubSetType, transportPlanTypeList)
                : fetchForEquipmentEvents(pubSetType, transportPlanTypeList);
        return mapOfTransportPlan
                .map(e -> e.get(getActLocation(equipmentFirstElement)))
                .orElse(null);
    }

    private Optional<Map<String, TransportPlanType>> fetchForTransportEvents(PubSetType pubSetType, List<TransportPlanType> transportPlanTypeList) {
        return Optional.ofNullable(getLocationAndTransportPlanForTransportEvents(transportPlanTypeList, pubSetType.getEvent()));
    }

    private Optional<Map<String, TransportPlanType>> fetchForEquipmentEvents(PubSetType pubSetType, List<TransportPlanType> transportPlanTypeList) {
        return Optional.ofNullable(getLocationAndTransportPlanForEquipmentEvents(transportPlanTypeList, pubSetType.getEvent()));
    }

    private boolean isETAorETDEvent(String eventAct) {
        return SHIPMENT_ETA.equals(eventAct) || SHIPMENT_ETD.equals(eventAct);
    }

    private static TransportCall createTransportCall(TransportPlanType transportPlan, EquipmentType equipmentFirstElement, String eventType, List<TransportPlanType> transportPlanList) {
        var transportCall = new TransportCall();
        transportCall.setCarrierServiceCode(getCarrierServiceCode(equipmentFirstElement));
        transportCall.setCarrierVoyageNumber(getVoyageNumberFromTransportPlan(transportPlan));
        transportCall.setModeOfTransport(getModeOfTransport(eventType, equipmentFirstElement, transportPlanList));
        transportCall.setOtherFacility(getLocation(equipmentFirstElement));
        return transportCall;
    }

    private static String getVoyageNumberFromTransportPlan(TransportPlanType transportPlanMap) {
        return Optional.ofNullable(transportPlanMap)
                .map(TransportPlanType::getVoyage)
                .orElse(null);

    }

    private static String getVesselCodeFromTransportPlan(TransportPlanType transportPlan) {
        return Optional.ofNullable(transportPlan)
                .map(TransportPlanType::getVesselCde)
                .orElse(null);
    }

    private static String getLocation(EquipmentType equipmentTypeElement) {
        return Optional.ofNullable(equipmentTypeElement.getMove())
                .map(MoveType::getActLoc)
                .orElse(null);
    }

    private static TransPortMode getModeOfTransport(String eventAct, EquipmentType equipmentFirstElement, List<TransportPlanType> transportPlanTypeList) {
        if (isVesselEvent(eventAct)) {
            return TransPortMode.VESSEL;
        } else if (isRailEvent(eventAct)) {
            return TransPortMode.RAIL;
        } else if (isTruckOrBargeEvent(eventAct)) {
            if (isBargeEvent(eventAct, equipmentFirstElement, transportPlanTypeList))
                return TransPortMode.BARGE;
            return TransPortMode.TRUCK;
        } else {
            return null;
        }
    }

    private static boolean isBargeEvent(String eventAct, EquipmentType equipmentFirstElement, List<TransportPlanType> transportPlanTypeList) {
        for (TransportPlanType transportPlanType : transportPlanTypeList) {
            var transportModeCode = Optional.ofNullable(transportPlanType)
                    .map(TransportPlanType::getTransMode)
                    .orElse("DEFAULT");
            if (eventAct.startsWith(GATE_IN)) {
                var endLoc = Optional.ofNullable(transportPlanType)
                        .map(TransportPlanType::getEndLoc)
                        .orElse(null);
                if (Objects.nonNull(getEndLocation(endLoc)) && isBargeTransportMode(transportModeCode, getEndLocation(endLoc), equipmentFirstElement))
                    return true;

            } else if (eventAct.startsWith(GATE_OUT)) {
                var startLoc = Optional.ofNullable(transportPlanType)
                        .map(TransportPlanType::getStartLoc)
                        .orElse(null);
                if (Objects.nonNull(getStartLocation(startLoc)) && isBargeTransportMode(transportModeCode, getStartLocation(startLoc), equipmentFirstElement))
                    return true;
            }
        }
        return false;
    }

    private static boolean isBargeTransportMode(String transportModeCode, String location, EquipmentType equipmentFirstElement) {
        return transportModeCode.equalsIgnoreCase("BAR") && Objects.nonNull(equipmentFirstElement.getMove()) &&
                Objects.nonNull(equipmentFirstElement.getMove().getActLoc()) && location.equalsIgnoreCase(equipmentFirstElement.getMove().getActLoc());
    }

    private static boolean isVesselEvent(String eventAct) {
        return eventAct.startsWith(LOAD) || eventAct.startsWith(DISCHARG) || eventAct.equals(CONTAINER_ARRIVAL) ||
                eventAct.equals(CONTAINER_DEPARTURE) || eventAct.equals(SHIPMENT_ETD) || eventAct.equals(SHIPMENT_ETA);
    }

    private static boolean isRailEvent(String eventAct) {
        return eventAct.equals(RAIL_ARRIVAL_AT_DESTINATION) || eventAct.equals(RAIL_DEPARTURE) ||
                eventAct.startsWith(OFF_RAIL) || eventAct.startsWith(ON_RAIL);
    }

    private static boolean isTruckOrBargeEvent(String eventAct) {
        return eventAct.startsWith(ARRIVECU) || eventAct.startsWith(DEPARTCU) || eventAct.startsWith(GATE_IN) ||
                eventAct.startsWith(GATE_OUT) || eventAct.startsWith(STRIPPIN) || eventAct.startsWith(STUFFING);
    }

    private static String getCarrierServiceCode(EquipmentType equipment) {
        if (Objects.nonNull(equipment.getMove()) &&
                Objects.nonNull(equipment.getMove().getLineCde())) {
            return equipment.getMove().getLineCde();
        }
        return null;
    }

    private static String getActLocation(EquipmentType firstEquipmentType) {
        if (Objects.nonNull(firstEquipmentType.getMove()) &&
                Objects.nonNull(firstEquipmentType.getMove().getActLoc())) {
            return firstEquipmentType.getMove().getActLoc();
        }
        return null;
    }

    private static Map<String, TransportPlanType> getLocationAndTransportPlanForTransportEvents(List<TransportPlanType> transportPlan, EventType eventType) {
        var typeOfTransport = getArrivalOrDepartureEventType(eventType.getEventAct());
        var transportPlans = getNonNullTransportPlans(transportPlan);
        return getTransportPlanMapBasedOnArrivalOrDepartureEvents(typeOfTransport, transportPlans);
    }

    private static Map<String, TransportPlanType> getLocationAndTransportPlanForEquipmentEvents(List<TransportPlanType> transportPlan, EventType eventType) {
        var arrivalOrDeparture = getArrivalOrDepartureEquipmentEvent(eventType.getEventAct());
        var transportPlans = getNonNullTransportPlans(transportPlan);
        return getTransportPlanMapBasedOnArrivalOrDepartureEvents(arrivalOrDeparture, transportPlans);
    }

    private static Map<String, TransportPlanType> getTransportPlanMapBasedOnArrivalOrDepartureEvents
            (MSK.com.external.dcsa.TransportEventType typeOfTransport, List<TransportPlanType> transportPlans) {
        if (typeOfTransport.equals(ARRI)) {
            return createMapOfEndLocationAndTransportPlans(transportPlans);
        } else if (typeOfTransport.equals(DEPA)) {
            return createMapOfStartLocationAndTransportPlans(transportPlans);
        }
        return Collections.emptyMap();
    }

    private static Map<String, TransportPlanType> createMapOfStartLocationAndTransportPlans(List<TransportPlanType> transportPlans) {
        return transportPlans
                .stream()
                .collect(Collectors.toMap(e -> getStartLocation(e.getStartLoc()), Function.identity()));
    }

    private static Map<String, TransportPlanType> createMapOfEndLocationAndTransportPlans(List<TransportPlanType> transportPlans) {
        return transportPlans
                .stream()
                .collect(Collectors.toMap(e -> getEndLocation(e.getEndLoc()), Function.identity()));
    }

    private static List<TransportPlanType> getNonNullTransportPlans(List<TransportPlanType> transportPlan) {
        return Optional.ofNullable(transportPlan)
                .filter(transportPlanTypes -> !transportPlanTypes.isEmpty())
                .orElseThrow(() -> new MappingException(TRANSPORT_PLAN_EMPTY));
    }

    private static String getEndLocation(EndLocType endLocType) {
        if (Objects.nonNull(endLocType)) {
            return (endLocType.getValue());
        }
        return null;
    }

    private static String getStartLocation(StartLocType startLocType) {
        if (Objects.nonNull(startLocType)) {
            return startLocType.getValue();
        }
        return null;
    }

}
