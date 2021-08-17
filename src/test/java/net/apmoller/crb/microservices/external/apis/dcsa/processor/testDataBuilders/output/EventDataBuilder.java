package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output;

import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References;

import java.util.List;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.MAEU;

public final class EventDataBuilder {


    private EventDataBuilder(){}


    public static Event getEventForShipmentEventType() {
        return getEventWithEventTypeAct(Event.EventType.SHIPMENT, "2021-09-23 23:45", getParties(),getReferences());
    }

    public static Event getEventForEquipmentEventType() {
        return getEventWithEventTypeAct(Event.EventType.EQUIPMENT, "2021-09-23 23:45", getParties(), getReferences());
    }

    public static Event getEventForTransportEventType() {
        return getEventWithEventTypeAct(Event.EventType.TRANSPORT, "2021-21-21 13:29", getParties2(), getReferences2());
    }
    public static Event getEventForTransportEventTypeWithESTEventAct() {
        return getEventWithEventTypeEst(Event.EventType.TRANSPORT, "2021-21-21 13:29", getParties3(), getReferences3());
    }

    private static Event getEventWithEventTypeAct(Event.EventType eventType, String eventDateTime, List<Party> parties, List<References> references) {
        return getEventBuilder(eventType, eventDateTime, parties, references)
                .eventClassifierCode(Event.EventClassifierCode.ACT)
                .build();
    }

    private static Event getEventWithEventTypeEst(Event.EventType eventType, String eventDateTime, List<Party> parties, List<References> references) {
        return getEventBuilder(eventType, eventDateTime, parties, references)
                .eventClassifierCode(Event.EventClassifierCode.EST)
                .build();
    }

    private static Event.EventBuilder<?, ?> getEventBuilder(Event.EventType eventType, String eventDateTime, List<Party> parties, List<References> references) {
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
        return Party.builder()
                .partyFunctionName(Party.PartyFuncName.BOOKED_BY)
                .partyID("26047945")
                .partyName("PARTY NAME 4")
                .partyFunctionCode(null)
                .build();
    }

    private static Party getParty3() {
        return Party.builder()
                .partyFunctionName(Party.PartyFuncName.OF)
                .partyID("80092546")
                .partyName("PARTY NAME 3")
                .partyFunctionCode(Party.PartyFuncCode.DDR)
                .build();
    }

    private static Party getParty2() {
        return Party.builder()
                .partyFunctionName(Party.PartyFuncName.CONSIGNEE)
                .partyID("241342126")
                .partyName("PARTY NAME 2")
                .partyFunctionCode(Party.PartyFuncCode.CN)
                .build();
    }

    private static Party getParty1() {
        return Party.builder()
                .partyFunctionName(Party.PartyFuncName.SHIPPER)
                .partyID("406019090921")
                .partyName("PARTY NAME 1")
                .partyFunctionCode(Party.PartyFuncCode.OS)
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
        return References.builder()
                .referenceType(References.RefTypeEnum.SI)
                .referenceValue("4351297464401500")
                .build();
    }

    private static References getRef2() {
        return References.builder()
                .referenceType(References.RefTypeEnum.AAO)
                .referenceValue("4351297464491500")
                .build();
    }

    private static References getRef3() {
        return References.builder()
                .referenceType(References.RefTypeEnum.FF)
                .referenceValue("4351297464471500")
                .build();
    }
}
