package net.apmoller.crb.microservices.external.apis.dcsa.processor.utils;


import MSK.com.external.dcsa.CarrierCode;
import MSK.com.external.dcsa.EquipmentEventType;
import MSK.com.external.dcsa.TransportEventType;
import com.maersk.jaxb.pojo.EquipmentType;
import com.maersk.jaxb.pojo.EventType;
import com.maersk.jaxb.pojo.MoveType;
import com.maersk.jaxb.pojo.PubSetType;
import com.maersk.jaxb.pojo.ShipmentType;
import com.maersk.jaxb.pojo.TPDocType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.dto.PartyFunctionDTO;
import org.springframework.data.mapping.MappingException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static MSK.com.external.dcsa.CarrierCode.MAEU;
import static MSK.com.external.dcsa.CarrierCode.MCCQ;
import static MSK.com.external.dcsa.CarrierCode.SAFM;
import static MSK.com.external.dcsa.CarrierCode.SEAU;
import static MSK.com.external.dcsa.CarrierCode.SEJJ;
import static MSK.com.external.dcsa.EquipmentEventType.DISC;
import static MSK.com.external.dcsa.EquipmentEventType.GTIN;
import static MSK.com.external.dcsa.EquipmentEventType.GTOT;
import static MSK.com.external.dcsa.EquipmentEventType.LOAD;
import static MSK.com.external.dcsa.EquipmentEventType.STRP;
import static MSK.com.external.dcsa.EquipmentEventType.STUF;
import static MSK.com.external.dcsa.PartyFuncName.ANP;
import static MSK.com.external.dcsa.PartyFuncName.AO;
import static MSK.com.external.dcsa.PartyFuncName.BOOKED_BY;
import static MSK.com.external.dcsa.PartyFuncName.CCIA;
import static MSK.com.external.dcsa.PartyFuncName.CCOA;
import static MSK.com.external.dcsa.PartyFuncName.CONSIGNEE;
import static MSK.com.external.dcsa.PartyFuncName.CONTRACT;
import static MSK.com.external.dcsa.PartyFuncName.CP;
import static MSK.com.external.dcsa.PartyFuncName.DMIP;
import static MSK.com.external.dcsa.PartyFuncName.DTIP;
import static MSK.com.external.dcsa.PartyFuncName.FNP;
import static MSK.com.external.dcsa.PartyFuncName.HC;
import static MSK.com.external.dcsa.PartyFuncName.HS;
import static MSK.com.external.dcsa.PartyFuncName.ICB;
import static MSK.com.external.dcsa.PartyFuncName.IDO;
import static MSK.com.external.dcsa.PartyFuncName.IF;
import static MSK.com.external.dcsa.PartyFuncName.IP;
import static MSK.com.external.dcsa.PartyFuncName.LH;
import static MSK.com.external.dcsa.PartyFuncName.OCB;
import static MSK.com.external.dcsa.PartyFuncName.ODO;
import static MSK.com.external.dcsa.PartyFuncName.OF;
import static MSK.com.external.dcsa.PartyFuncName.PO;
import static MSK.com.external.dcsa.PartyFuncName.RTP;
import static MSK.com.external.dcsa.PartyFuncName.SHIPPER;
import static MSK.com.external.dcsa.PartyFuncName.SUPP;
import static MSK.com.external.dcsa.PartyFuncName.SWCO;
import static MSK.com.external.dcsa.PartyFuncName.SWSH;
import static MSK.com.external.dcsa.PartyFuncName.TDR;
import static MSK.com.external.dcsa.PartyFunctionCode.CN;
import static MSK.com.external.dcsa.PartyFunctionCode.COW;
import static MSK.com.external.dcsa.PartyFunctionCode.DDR;
import static MSK.com.external.dcsa.PartyFunctionCode.DDS;
import static MSK.com.external.dcsa.PartyFunctionCode.N1;
import static MSK.com.external.dcsa.PartyFunctionCode.N2;
import static MSK.com.external.dcsa.PartyFunctionCode.OS;
import static MSK.com.external.dcsa.TransportEventType.ARRI;
import static MSK.com.external.dcsa.TransportEventType.DEPA;
import static java.util.Map.entry;


public final class EventUtility {

    public static final String TRANSPORT_EVENT = "TRANSPORT";
    public static final String EQUIPMENT_EVENT = "EQUIPMENT";
    public static final String SHIPMENT_EVENT = "SHIPMENT";


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

    public static TransportEventType getArrivalOrDepartureEventType(String eventAct) {

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
                throw new MappingException("Could not map Transport Event Type of ".concat(eventAct));
        }
    }


    public static TransportEventType getTPEventTypeFromPubSetType(PubSetType pubSetType) {
        var eventAct = Optional.ofNullable(pubSetType.getEvent())
                .map(EventType::getEventAct)
                .map(CharSequence::toString)
                .orElse("");
        return getArrivalOrDepartureEventType(eventAct);
    }

    public static EquipmentEventType getEquipmentEventType(String eventAct) {
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
                throw new MappingException("Could not map Equipment Event Type of ".concat(eventAct));
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
                .map(CharSequence::toString)
                .orElse("")
                .toUpperCase();
    }

}
