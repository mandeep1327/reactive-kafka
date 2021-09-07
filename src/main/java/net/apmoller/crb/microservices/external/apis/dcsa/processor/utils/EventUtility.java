package net.apmoller.crb.microservices.external.apis.dcsa.processor.utils;


import MSK.com.external.dcsa.CarrierCode;
import MSK.com.external.dcsa.TransportEventType;
import MSK.com.gems.EquipmentType;
import MSK.com.gems.EventType;
import MSK.com.gems.MoveType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.ShipmentType;
import MSK.com.gems.TPDocType;
import MSK.com.gems.TransportPlanType;
import lombok.experimental.UtilityClass;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.PartyFunctionDTO;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.CarrierCode.MAEU;
import static MSK.com.external.dcsa.CarrierCode.MCCQ;
import static MSK.com.external.dcsa.CarrierCode.SAFM;
import static MSK.com.external.dcsa.CarrierCode.SEAU;
import static MSK.com.external.dcsa.CarrierCode.SEJJ;
import static MSK.com.external.dcsa.PartyFunctionCode.CN;
import static MSK.com.external.dcsa.PartyFunctionCode.DDR;
import static MSK.com.external.dcsa.PartyFunctionCode.DDS;
import static MSK.com.external.dcsa.PartyFunctionCode.N1;
import static MSK.com.external.dcsa.PartyFunctionCode.N2;
import static MSK.com.external.dcsa.PartyFunctionCode.OS;
import static MSK.com.external.dcsa.TransportEventType.ARRI;
import static MSK.com.external.dcsa.TransportEventType.DEPA;
import static MSK.com.external.dcsa.TransportEventType.OMIT;
import static java.util.Map.entry;
import static java.util.Objects.isNull;

@UtilityClass
public final class EventUtility {

    public static final String ARRANGE_CARGO_RELEASE_CLOSED = "Arrange_Cargo_Release_Closed";
    public static final String CONFIRM_SHIPMENT_CLOSED = "Confirm_Shipment_Closed";
    public static final String SHIPMENT_CANCELLED = "Shipment_Cancelled";
    public static final String RECEIVE_TRANSPORT_DOCUMENT_INSTRUCTIONS_CLOSED = "Receive_Transport_Document_Instructions_Closed";
    public static final String ISSUE_ORIGINAL_TPDOC_CLOSED = "Issue_Original_TPDOC_Closed";
    public static final String ISSUE_VERIFY_COPY_OF_TPDOC_CLOSED = "Issue_Verify_Copy_of_TPDOC_Closed";
    public static final String RELEASE = "RELEASE";
    public static final String ARRANGE_CARGO_RELEASE_OPEN = "Arrange_Cargo_Release_Open";
    public static final String ARRIVAL_NOTICE = "ARRIVAL_NOTICE";
    public static final String SHIPMENT_ETA = "Shipment_ETA";
    public static final String SHIPMENT_ETD = "Shipment_ETD";
    public static final String RAIL_ARRIVAL_AT_DESTINATION = "RAIL_ARRIVAL_AT_DESTINATION";
    public static final String RAIL_DEPARTURE = "RAIL_DEPARTURE";
    public static final String ARRIVECUIMPN = "ARRIVECUIMPN";
    public static final String GATE_IN_EXPN = "GATE-IN EXPN";
    public static final String OFF_RAILIMPN = "OFF-RAILIMPN";
    public static final String DEPARTCUEXPN = "DEPARTCUEXPN";
    public static final String GATE_OUTEXPY = "GATE-OUTEXPY";
    public static final String ON_RAIL_EXPN = "ON-RAIL EXPN";
    public static final String LOAD_N = "LOAD       N";
    public static final String STUFFINGEXPN = "STUFFINGEXPN";
    public static final String DISCHARG_N = "DISCHARG   N";
    public static final String STRIPPIN_Y = "STRIPPIN   Y";
    public static final String CONTAINER_ARRIVAL = "CONTAINER ARRIVAL";
    public static final String CONTAINER_DEPARTURE = "CONTAINER DEPARTURE";


    public static final List<String> SHIPMENT_EVENTS = List.of(
            CONFIRM_SHIPMENT_CLOSED,
            SHIPMENT_CANCELLED,
            RECEIVE_TRANSPORT_DOCUMENT_INSTRUCTIONS_CLOSED,
            ARRANGE_CARGO_RELEASE_CLOSED,
            ARRANGE_CARGO_RELEASE_OPEN,
            ISSUE_ORIGINAL_TPDOC_CLOSED,
            ISSUE_VERIFY_COPY_OF_TPDOC_CLOSED,
            ARRIVAL_NOTICE,
            RELEASE
    );

    public static final List<String> TRANSPORT_EVENTS = List.of(
            CONTAINER_ARRIVAL,
            CONTAINER_DEPARTURE,
            RAIL_ARRIVAL_AT_DESTINATION,
            RAIL_DEPARTURE,
            RAIL_ARRIVAL_AT_DESTINATION,
            RAIL_DEPARTURE,
            SHIPMENT_ETA,
            SHIPMENT_ETD
    );


    public static final List<String> EQUIPMENT_EVENTS = List.of(
            ARRIVECUIMPN,
            DEPARTCUEXPN,
            DISCHARG_N,
            GATE_IN_EXPN,
            GATE_OUTEXPY,
            LOAD_N,
            OFF_RAILIMPN,
            ON_RAIL_EXPN,
            STRIPPIN_Y,
            STUFFINGEXPN
    );

    public static final List<String> EST_EVENTS = List.of(
            SHIPMENT_ETA,
            SHIPMENT_ETD
    );

    public static final List<String> ACT_EVENTS = List.of(
            RAIL_ARRIVAL_AT_DESTINATION,
            ARRIVECUIMPN,
            DEPARTCUEXPN,
            RAIL_DEPARTURE,
            ARRANGE_CARGO_RELEASE_CLOSED,
            CONFIRM_SHIPMENT_CLOSED,
            SHIPMENT_CANCELLED,
            RECEIVE_TRANSPORT_DOCUMENT_INSTRUCTIONS_CLOSED,
            ISSUE_ORIGINAL_TPDOC_CLOSED,
            ISSUE_VERIFY_COPY_OF_TPDOC_CLOSED,
            RELEASE,
            ARRANGE_CARGO_RELEASE_OPEN,
            ARRIVAL_NOTICE,
            DISCHARG_N,
            GATE_OUTEXPY,
            LOAD_N,
            GATE_IN_EXPN,
            OFF_RAILIMPN,
            ON_RAIL_EXPN,
            ARRIVECUIMPN,
            DEPARTCUEXPN,
            STUFFINGEXPN,
            STRIPPIN_Y,
            CONTAINER_ARRIVAL,
            CONTAINER_DEPARTURE
    );

    public static TransportEventType getArrivalOrDepartureEventType(String eventAct) {

        if (isNull(eventAct)) {
            throw new MappingException("Null Event Act");
        }

        switch (eventAct) {
            case ARRIVECUIMPN:
            case CONTAINER_ARRIVAL:
            case RAIL_ARRIVAL_AT_DESTINATION:
            case SHIPMENT_ETA:
                return ARRI;
            case CONTAINER_DEPARTURE:
            case DEPARTCUEXPN:
            case RAIL_DEPARTURE:
            case SHIPMENT_ETD:
                return DEPA;
            default:
                throw new MappingException("Could not map Transport Event Type of ".concat(eventAct));
        }
    }

    public static TransportEventType getArrivalOrDepartureEquipmentEvent(String eventAct) {

        if (isNull(eventAct)) {
            throw new MappingException("Null Event Act");
        }

        switch (eventAct) {
            case DISCHARG_N:
            case OFF_RAILIMPN:
                return ARRI;
            case GATE_IN_EXPN:
            case GATE_OUTEXPY:
            case LOAD_N:
            case ON_RAIL_EXPN:
            case STUFFINGEXPN:
                return DEPA;
            default:
               return OMIT;
        }
    }


    public static TransportEventType getTPEventTypeFromPubSetType(PubSetType pubSetType) {
        var eventAct = getEventAct(pubSetType);
        return getArrivalOrDepartureEventType(eventAct);
    }


    public static EquipmentType getFirstEquipmentElement (PubSetType pubSetType){
        return Optional.ofNullable(pubSetType)
                .map(PubSetType::getEquipment)
                .filter(equipmentTypes -> !equipmentTypes.isEmpty())
                .map(equipmentList -> equipmentList.get(0))
                .orElse(new EquipmentType());
    }

    public static Map<Integer, PartyFunctionDTO> getMapOfPartyRoleAndFunctions (){
        return Map.ofEntries(
                entry(3, PartyFunctionDTO.builder().functionName("Shipper").functionCode(OS).build()),
                entry(4, PartyFunctionDTO.builder().functionName("Consignee").functionCode(CN).build()),
                entry(5, PartyFunctionDTO.builder().functionName("First Notify Party").functionCode(N1).build()),
                entry(6, PartyFunctionDTO.builder().functionName("Additional Notify Party").functionCode(N2).build()),
                entry(15, PartyFunctionDTO.builder().functionName("Outward Forwarder").functionCode(DDR).build()),
                entry(16, PartyFunctionDTO.builder().functionName("Inward Forwarder").functionCode(DDS).build())
        );
    }

    public static String getSourceSystemFromPubsetType(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getEvent())
                .map(EventType::getSrcSys)
                .orElse(null);
    }

    public static Optional<TransportPlanType> getLastTransportPlanWithPortOfDischarge(PubSetType pubSetType) {
        return getTransportPlanTypeForPoLNPoD(pubSetType)
                .reduce((first, second) -> second);
    }

    public static Optional<TransportPlanType> getFirstTransportPlanTypeWithPortOfLoad(PubSetType pubSetType) {
        return getTransportPlanTypeForPoLNPoD(pubSetType)
                .findFirst();
    }

    private static Stream<TransportPlanType> getTransportPlanTypeForPoLNPoD(PubSetType pubSetType) {
        return getTransportPlanTypeStream(pubSetType)
                .filter(getTransportPlanTypePredicate());
    }

    private static Stream<TransportPlanType> getTransportPlanTypeStream(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType)
                .map(PubSetType::getTransportPlan)
                .filter(transportPlanTypes -> !transportPlanTypes.isEmpty())
                .orElse(new ArrayList<>())
                .stream();
    }

    private static Predicate<TransportPlanType> getTransportPlanTypePredicate() {
        var transportPlanList  = List.of("FEF", "FEO", "MVS", "VSF", "VSM", "VSL");
        return transportPlanType -> transportPlanType.getTransMode() != null && transportPlanList.contains(transportPlanType.getTransMode());
    }

    public static String getBookingNumber(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getBookNo)
                .orElse(null);
    }

    public static String getBolNumber(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getTpdoc())
                .filter(tpDocTypes -> !tpDocTypes.isEmpty())
                .map(tpDocTypes -> tpDocTypes.get(0))
                .map(TPDocType::getBolNo)
                .orElse(null);
    }

    public static String fromPubSetTypeToEquipmentReference(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getEqptNo())
                .orElse(null);
    }

    public static CarrierCode fromPubSetTypeToCarrierCode(PubSetType pubSetType) {

        switch (getCarrierCodes(pubSetType)) {
            case "MSK":
            case "MSL":
                return MAEU;
            case "SCL":
            case "SAF":
                return SAFM;
            case "MCC":
                return MCCQ;
            case "SGL":
                return SEJJ;
            case "SEL":
                return SEAU;
            default:
                return null;
        }
    }

    protected static String getCarrierCodes(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getMove())
                .map(MoveType::getOperator)
                .orElse("")
                .toUpperCase();
    }

    public static String getEventAct(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType)
                .map(PubSetType::getEvent)
                .map(EventType::getEventAct)
                .orElseThrow(() -> new MappingException("The pubset data does not contain any Event Act"));
    }

}