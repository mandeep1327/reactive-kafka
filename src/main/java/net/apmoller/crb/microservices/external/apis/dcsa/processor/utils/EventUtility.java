package net.apmoller.crb.microservices.external.apis.dcsa.processor.utils;


import com.maersk.jaxb.pojo.EquipmentType;
import com.maersk.jaxb.pojo.EventType;
import com.maersk.jaxb.pojo.MoveType;
import com.maersk.jaxb.pojo.PubSetType;
import com.maersk.jaxb.pojo.ShipmentType;
import com.maersk.jaxb.pojo.TPDocType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportEvent;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.dto.PartyFunctionDTO;
import org.springframework.data.mapping.MappingException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Map.entry;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.DISC;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.GTIN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.GTOT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.LOAD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.STRP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EquipmentEventType.STUF;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.MAEU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.MCCQ;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.SAFM;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.SEAU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.SEJJ;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.CN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.COW;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.DDR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.DDS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.N1;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.N2;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncCode.OS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.ANP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.AO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.BOOKED_BY;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.CCIA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.CCOA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.CONSIGNEE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.CONTRACT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.CP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.DMIP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.DTIP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.FNP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.HC;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.HS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.ICB;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.IDO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.IF;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.IP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.LH;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.OCB;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.ODO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.OF;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.PO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.RTP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.SHIPPER;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.SUPP;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.SWCO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.SWSH;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party.PartyFuncName.TDR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportEvent.TransportEventType.ARRI;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportEvent.TransportEventType.DEPA;

public final class EventUtility {

    public final static String TRANSPORT_EVENT = "TRANSPORT";
    public final static String EQUIPMENT_EVENT = "EQUIPMENT";
    public final static String SHIPMENT_EVENT = "SHIPMENT";




    private EventUtility() {
    }

    public static final List<String> SHIPMENT_EVENTS = List.of(
            "Confirm_Shipment_Closed",
            "Shipment_Cancelled",
            "Receive_Transport_Document_Instructions_Closed",
            "Equipment_VGM_Details_Updated",
            "Arrange_Cargo_Release_Closed",
            "Arrange_Cargo_Release_Open",
            "Issue_Original_TPDOC_Closed",
            "Issue_Verify_Copy_of_TPDOC_Closed",
            "ARRIVAL_NOTICE",
            "RELEASE");

    public static final List<String> TRANSPORT_EVENTS = List.of(
            "CONTAINER ARRIVAL",
            "CONTAINER DEPARTURE",
            "RAIL_ARRIVAL_AT_DESTINATION",
            "RAIL_DEPARTURE",
            "RAIL_ARRIVAL_AT_DESTINATION",
            "RAIL_DEPARTURE",
            "Shipment_ETA",
            "Shipment_ETD");

    public static final List<String> EQUIPMENT_EVENTS = List.of(
            "ARRIVECUIMPN",
            "DEPARTCUEXPN",
            "DISCHARG   N",
            "GATE-IN EXPN",
            "GATE-OUTEXPY",
            "LOAD       N",
            "OFF-RAILIMPN",
            "ON-RAIL EXPN",
            "STRIPPIN   Y",
            "STUFFINGEXPN");

    public static final List<String> EST_EVENTS = List.of(
            "Shipment_ETA",
            "Shipment_ETD");

    public static final List<String> ACT_EVENTS = List.of(
            "RAIL_ARRIVAL_AT_DESTINATION",
            "ARRIVECUIMPN",
            "DEPARTCUEXPN",
            "RAIL_DEPARTURE",
            "Arrange_Cargo_Release_Closed",
            "Confirm_Shipment_Closed",
            "Shipment_Cancelled",
            "Receive_Transport_Document_Instructions_Closed",
            "Equipment_VGM_Details_Updated",
            "Issue_Original_TPDOC_Closed",
            "Issue_Verify_Copy_of_TPDOC_Closed",
            "RELEASE",
            "Arrange_Cargo_Release_Open",
            "ARRIVAL_NOTICE",
            "DISCHARG   N",
            "GATE-OUTEXPY",
            "LOAD       N",
            "GATE-IN EXPN",
            "OFF-RAILIMPN",
            "ON-RAIL EXPN",
            "ARRIVECUIMPN",
            "DEPARTCUEXPN",
            "STUFFINGEXPN",
            "STRIPPIN   Y",
            "CONTAINER ARRIVAL",
            "CONTAINER DEPARTURE"
    );

    public static TransportEvent.TransportEventType getArrivalOrDepartureEventType(String eventAct) {

        if (eventAct.isBlank()) {
            throw new MappingException("Null Event Act");
        }

        switch (eventAct) {
            case "ARRIVECUIMPN":
            case "CONTAINER ARRIVAL":
            case "RAIL_ARRIVAL_AT_DESTINATION":
            case "Shipment_ETA":
                return ARRI;
            case "CONTAINER DEPARTURE":
            case "DEPARTCUEXPN":
            case "RAIL_DEPARTURE":
            case "Shipment_ETD":
                return DEPA;
            default:
                throw new MappingException("Could not map Shipment Event Type of ".concat(eventAct));
        }
    }


    public static TransportEvent.TransportEventType getTPEventTypeFromPubSetType(PubSetType pubSetType) {
        var eventAct = Optional.ofNullable(pubSetType.getEvent())
                .map(EventType::getEventAct)
                .map(CharSequence::toString)
                .orElse("");
        return getArrivalOrDepartureEventType(eventAct);
    }

    public static EquipmentEvent.EquipmentEventType getEquipmentEventType(String eventAct) {
        if (Objects.isNull(eventAct)) {
            throw new MappingException("Null Event Act");
        }

        switch (eventAct) {
            case "ARRIVECUIMPN":
            case "GATE-IN EXPN":
            case "OFF-RAILIMPN":
                return GTIN;
            case "DISCHARG   N":
                return DISC;
            case "DEPARTCUEXPN":
            case "GATE-OUTEXPY":
            case "ON-RAIL EXPN":
                return GTOT;
            case "LOAD       N":
                return LOAD;
            case "STRIPPIN   Y":
                return STRP;
            case "STUFFINGEXPN":
                return STUF;
            default:
                throw new MappingException("Could not map Shipment Event Type of ".concat(eventAct));
        }
    }

    public static EquipmentType getFirstEquipmentElement (PubSetType pubSetType){
        return Optional.ofNullable(pubSetType)
                .map(PubSetType::getEquipment)
                .filter(equipmentTypes -> !equipmentTypes.isEmpty())
                .map(equipmentList -> equipmentList.get(0))
                .orElseThrow(() -> new MappingException("Equipment element is missing"));
    }

    public static Map<Integer, PartyFunctionDTO> getMapOfPartyRoleAndFunctions (){
        return Map.ofEntries(
                entry(1, PartyFunctionDTO.builder().functionName(BOOKED_BY).build()),
                entry(2, PartyFunctionDTO.builder().functionName(CONTRACT).build()),
                entry(3, PartyFunctionDTO.builder().functionName(SHIPPER).functionCode(OS).build()),
                entry(4, PartyFunctionDTO.builder().functionName(CONSIGNEE).functionCode(CN).build()),
                entry(5, PartyFunctionDTO.builder().functionName(FNP).functionCode(N1).build()),
                //TODO: special case is there with 6
                entry(6, PartyFunctionDTO.builder().functionName(ANP).functionCode(N2).build()),
                entry(7, PartyFunctionDTO.builder().functionName(AO).build()),
                entry(11, PartyFunctionDTO.builder().functionName(OCB).build()),
                entry(12, PartyFunctionDTO.builder().functionName(ICB).build()),
                entry(13, PartyFunctionDTO.builder().functionName(CCIA).build()),
                //TODO: special entry for 14
                entry(14, PartyFunctionDTO.builder().functionName(IP).functionCode(COW).build()),
                entry(15, PartyFunctionDTO.builder().functionName(OF).functionCode(DDR).build()),
                entry(16, PartyFunctionDTO.builder().functionName(IF).functionCode(DDS).build()),
                entry(22, PartyFunctionDTO.builder().functionName(TDR).build()),
                entry(25, PartyFunctionDTO.builder().functionName(CCOA).build()),
                entry(26, PartyFunctionDTO.builder().functionName(CP).build()),
                entry(27, PartyFunctionDTO.builder().functionName(PO).build()),
                entry(28, PartyFunctionDTO.builder().functionName(ODO).build()),
                entry(29, PartyFunctionDTO.builder().functionName(IDO).build()),
                entry(31, PartyFunctionDTO.builder().functionName(RTP).build()),
                entry(32, PartyFunctionDTO.builder().functionName(LH).build()),
                entry(33, PartyFunctionDTO.builder().functionName(DMIP).build()),
                entry(34, PartyFunctionDTO.builder().functionName(DTIP).build()),
                entry(35, PartyFunctionDTO.builder().functionName(SUPP).build()),
                entry(36, PartyFunctionDTO.builder().functionName(HS).build()),
                entry(37, PartyFunctionDTO.builder().functionName(HC).build()),
                entry(38, PartyFunctionDTO.builder().functionName(SWSH).build()),
                entry(39, PartyFunctionDTO.builder().functionName(SWCO).build())
        );
    }

    public static String getSourceSystemFromPubsetType(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getEvent())
                .map(EventType::getSrcSys)
                .map(CharSequence::toString)
                .orElse(null);
    }

    public static String getBookingNumber(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getBookNo)
                .map(CharSequence::toString)
                .orElse(null);
    }

    public static String getBolNumber(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getTpdoc())
                .filter(tpDocTypes -> !tpDocTypes.isEmpty())
                .map(tpDocTypes -> tpDocTypes.get(0))
                .map(TPDocType::getBolNo)
                .map(CharSequence::toString)
                .orElse(null);
    }

    public static String fromPubSetTypeToEquipmentReference(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getEqptNo())
                .map(CharSequence::toString)
                .orElse(null);
    }

    public static EquipmentEvent.CarrierCodeEnum fromPubSetTypeToCarrierCode(PubSetType pubSetType) {

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
                .map(CharSequence::toString)
                .orElse("")
                .toUpperCase();
    }

}
