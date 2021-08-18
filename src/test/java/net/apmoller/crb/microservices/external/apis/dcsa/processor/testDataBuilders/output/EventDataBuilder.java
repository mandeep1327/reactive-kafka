package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output;

import MSK.com.external.dcsa.EventClassifierCode;
import MSK.com.external.dcsa.EventType;
import MSK.com.external.dcsa.Party;
import MSK.com.external.dcsa.PartyFuncName;
import MSK.com.external.dcsa.PartyFunctionCode;
import MSK.com.external.dcsa.RefTypeEnum;
import MSK.com.external.dcsa.References;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;

import java.util.List;

import static MSK.com.external.dcsa.CarrierCode.MAEU;


public final class EventDataBuilder {


    private EventDataBuilder(){}


    public static Event getEventForShipmentEventType() {
        return getEventWithEventTypeAct(EventType.SHIPMENT, "2021-09-23 23:45", getParties(),getReferences());
    }

    public static Event getEventForEquipmentEventType() {
        return getEventWithEventTypeAct(EventType.EQUIPMENT, "2021-09-23 23:45", getParties(), getReferences());
    }

    public static Event getEventForTransportEventType() {
        return getEventWithEventTypeAct(EventType.TRANSPORT, "2021-21-21 13:29", getParties2(), getReferences2());
    }
    public static Event getEventForTransportEventTypeWithESTEventAct() {
        return getEventWithEventTypeEst( "2021-21-21 13:29", getParties3(), getReferences3());
    }

    private static Event getEventWithEventTypeAct(EventType eventType, String eventDateTime, List<Party> parties, List<References> references) {
        return getEventBuilder(eventType, eventDateTime, parties, references)
                .eventClassifierCode(EventClassifierCode.ACT)
                .build();
    }

    private static Event getEventWithEventTypeEst( String eventDateTime ,List<Party> parties, List<References> references) {
        return getEventBuilder(EventType.TRANSPORT, eventDateTime, parties, references)
                .eventClassifierCode(EventClassifierCode.EST)
                .build();
    }

    private static Event.EventBuilder<?, ?> getEventBuilder(EventType eventType, String eventDateTime, List<Party> parties, List<References> references) {
        return Event.builder()
                .bookingReference("209989099")
                .eventID("2537152461542365")
                .eventType(eventType)
                .eventDateTime(eventDateTime)
                .eventCreatedDateTime("2021-09-23 23:45")
                .references(references)
                .carrierBookingReference("209989099")
                .transportDocumentReference("293156737")
                .equipmentReference("TCNU6816701")
                .sourceSystem("GCSS")
                .carrierCode(MAEU)
                .serviceType("CY/CY")
                .parties(parties);
    }

    private static List<Party> getParties() {
        return List.of(getParty1(), getParty2());
    }

    private static List<Party> getParties2() {
        return List.of(getParty2(), getParty3());
    }

    private static List<Party> getParties3() {
        return List.of(getParty3(), getParty4());
    }

    private static Party getParty4() {
        return Party.newBuilder()
                .setPartyFunctionName(PartyFuncName.BOOKED_BY)
                .setPartyID("26047945")
                .setPartyName("PARTY NAME 4")
                .setPartyFunctionCode(null)
                .build();
    }

    private static Party getParty3() {
        return Party.newBuilder()
                .setPartyFunctionName(PartyFuncName.OF)
                .setPartyID("80092546")
                .setPartyName("PARTY NAME 3")
                .setPartyFunctionCode(PartyFunctionCode.DDR)
                .build();
    }

    private static Party getParty2() {
        return Party.newBuilder()
                .setPartyFunctionName(PartyFuncName.CONSIGNEE)
                .setPartyID("241342126")
                .setPartyName("PARTY NAME 2")
                .setPartyFunctionCode(PartyFunctionCode.CN)
                .build();
    }

    private static Party getParty1() {
        return Party.newBuilder()
                .setPartyFunctionName(PartyFuncName.SHIPPER)
                .setPartyID("406019090921")
                .setPartyName("PARTY NAME 1")
                .setPartyFunctionCode(PartyFunctionCode.OS)
                .build();

    }

    private static List<References> getReferences() {
        return List.of(getRef1());
    }

    private static List<References> getReferences2() {
        return List.of(getRef2());
    }

    private static List<References> getReferences3() {
        return List.of(getRef3());
    }

    private static References getRef1() {
        return References.newBuilder()
                .setReferenceType(RefTypeEnum.SI)
                .setReferenceValue("4351297464401500")
                .build();
    }

    private static References getRef2() {
        return References.newBuilder()
                .setReferenceType(RefTypeEnum.AAO)
                .setReferenceValue("4351297464491500")
                .build();
    }

    private static References getRef3() {
        return References.newBuilder()
                .setReferenceType(RefTypeEnum.FF)
                .setReferenceValue("4351297464471500")
                .build();
    }
}
