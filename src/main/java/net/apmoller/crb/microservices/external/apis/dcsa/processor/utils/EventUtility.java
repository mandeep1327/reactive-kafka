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
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.PartyFunctionDTO;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
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
import static java.util.Objects.nonNull;

@UtilityClass
@Slf4j
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
    public static final String CONTAINER_ARRIVAL = "CONTAINER ARRIVAL";
    public static final String CONTAINER_DEPARTURE = "CONTAINER DEPARTURE";

    public static final String ARRIVECU = "ARRIVECU";
    public static final String DEPARTCU = "DEPARTCU";
    public static final String DISCHARG = "DISCHARG";
    public static final String GATE_IN = "GATE-IN";
    public static final String GATE_OUT = "GATE-OUT";
    public static final String LOAD = "LOAD";
    public static final String OFF_RAIL = "OFF-RAIL";
    public static final String ON_RAIL = "ON-RAIL";
    public static final String STRIPPIN = "STRIPPIN";
    public static final String STUFFING = "STUFFING";

    public static final DateTimeFormatter OUTBOUND_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final DateTimeFormatter INBOUND_TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            .toFormatter();

    public static String getTimeStampInUTCFormat(String originalTimestamp){
        if (nonNull(originalTimestamp) && !originalTimestamp.isEmpty()) {
            LocalDateTime parsedDateAndTime = getLocalDateTime(originalTimestamp);
            return parsedDateAndTime.format(OUTBOUND_FORMATTER);
        }
        return null;
    }

    private static LocalDateTime getLocalDateTime(String originalTimestamp) {
        try {
            return LocalDateTime.parse(originalTimestamp, INBOUND_TIMESTAMP_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("Received an non-resolvable time and date from Gems {}", originalTimestamp );
            throw e;
        }
    }


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


    public static final List<String> EQUIPMENT_EVENTS_PREFIX = List.of(
            ARRIVECU,
            DEPARTCU,
            DISCHARG,
            GATE_IN,
            GATE_OUT,
            LOAD,
            OFF_RAIL,
            ON_RAIL,
            STRIPPIN,
            STUFFING
    );

    public static final List<String> EST_EVENTS = List.of(
            SHIPMENT_ETA,
            SHIPMENT_ETD
    );

    public static TransportEventType getArrivalOrDeparture(String eventAct) {
        if (nonNull(eventAct)) {
            if (isARRI(eventAct)) {
                return ARRI;
            } else if (isDEPA(eventAct)) {
                return DEPA;
            }
        }
        return OMIT;
    }

    private static boolean isARRI(String eventAct) {
        return SHIPMENT_ETA.equals(eventAct) || RAIL_ARRIVAL_AT_DESTINATION.equals(eventAct) ||
                CONTAINER_ARRIVAL.equals(eventAct) || eventAct.startsWith(ARRIVECU) ||
                eventAct.startsWith(DISCHARG) || eventAct.startsWith(OFF_RAIL);
    }

    private static boolean isDEPA(String eventAct) {
        return SHIPMENT_ETD.equals(eventAct) || RAIL_DEPARTURE.equals(eventAct) ||
                CONTAINER_DEPARTURE.equals(eventAct) || eventAct.startsWith(DEPARTCU) ||
                eventAct.startsWith(GATE_IN) || eventAct.startsWith(GATE_OUT) ||
                eventAct.startsWith(STUFFING) || eventAct.startsWith(LOAD) ||
                eventAct.startsWith(ON_RAIL);
    }

    public static TransportEventType getTPEventTypeFromPubSetType(PubSetType pubSetType) {
        var eventAct = getEventAct(pubSetType);
        return getArrivalOrDeparture(eventAct);
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

    private static String getCarrierCodes(PubSetType pubSetType) {
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

    /**
     * To be classified as an equipment event it is enough that the
     * event start with a string in the defined list. The end of the
     * event string defines a sub event type, we are only interested in
     * the main event here
     */
    public static boolean isEquipmentEvent(String string) {
        return EQUIPMENT_EVENTS_PREFIX.stream().anyMatch(string::startsWith);
    }
}