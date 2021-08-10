package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output;

import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Party;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References;

import java.util.List;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event.CarrierCodeEnum.MAEU;

public final class EventDataBuilder {


    private EventDataBuilder(){}


    public static Event getEventForShipmentEventType() {
        return getEventWithEventTypeAct(Event.EventType.SHIPMENT, "2021-09-23 23:45");
    }

    public static Event getEventForEquipmentEventType() {
        return getEventWithEventTypeAct(Event.EventType.EQUIPMENT, "2021-09-23 23:45");
    }

    public static Event getEventForTransportEventType() {
        return getEventWithEventTypeAct(Event.EventType.TRANSPORT, "2021-21-21 13:29");
    }
    public static Event getEventForTransportEventTypeWithESTEventAct() {
        return getEventWithEventTypeEst(Event.EventType.TRANSPORT, "2021-21-21 13:29");
    }

    private static Event getEventWithEventTypeAct(Event.EventType eventType, String eventDateTime) {
        return getEventBuilder(eventType, eventDateTime)
                .eventClassifierCode(Event.EventClassifierCode.ACT)
                .build();
    }

    private static Event getEventWithEventTypeEst(Event.EventType eventType, String eventDateTime) {
        return getEventBuilder(eventType, eventDateTime)
                .eventClassifierCode(Event.EventClassifierCode.EST)
                .build();
    }

    private static Event.EventBuilder<?, ?> getEventBuilder(Event.EventType eventType, String eventDateTime) {
        return Event.builder()
                .bookingReference("209989099")
                .eventID("2537152461542365")
                .eventType(eventType)
                .eventDateTime(eventDateTime)
                .eventCreatedDateTime("2021-09-23 23:45")
                .references(getReferences())
                .carrierBookingReference("209989099")
                .transportDocumentReference("293156737")
                .equipmentReference("TCNU6816701")
                .sourceSystem("GCSS")
                .carrierCode(MAEU)
                .serviceType("CY/CY")
                .parties(getParties());
    }

    private static List<Party> getParties() {
        return List.of(getParty1(), getParty2());
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

    private static References getRef1() {
        return References.builder()
                .referenceType(References.RefTypeEnum.SI)
                .referenceValue("4351297464401500")
                .build();
    }
}
