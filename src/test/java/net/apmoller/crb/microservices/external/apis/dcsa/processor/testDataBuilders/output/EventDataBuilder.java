package net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output;

import MSK.com.external.dcsa.EventClassifierCode;
import MSK.com.external.dcsa.EventType;
import MSK.com.external.dcsa.Party;
import MSK.com.external.dcsa.PartyFuncName;
import MSK.com.external.dcsa.PartyFunctionCode;
import MSK.com.external.dcsa.RefTypeEnum;
import MSK.com.external.dcsa.References;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;

import java.util.List;

import static MSK.com.external.dcsa.CarrierCode.MAEU;


public final class EventDataBuilder {


    private EventDataBuilder(){}


    public static Event getEventForShipmentEventType() {
        return getEventWithEventTypeAct(EventType.SHIPMENT, "2021-09-23 23:45");
    }

    public static Event getEventForEquipmentEventType() {
        return getEventWithEventTypeAct(EventType.EQUIPMENT, "2021-09-23 23:45");
    }

    public static Event getEventForTransportEventType() {
        return getEventWithEventTypeAct(EventType.TRANSPORT, "2021-21-21 13:29");
    }
    public static Event getEventForTransportEventTypeWithESTEventAct() {
        return getEventWithEventTypeEst(EventType.TRANSPORT, "2021-21-21 13:29");
    }

    private static Event getEventWithEventTypeAct(EventType eventType, String eventDateTime) {
        return getEventBuilder(eventType, eventDateTime)
                .eventClassifierCode(EventClassifierCode.ACT)
                .build();
    }

    private static Event getEventWithEventTypeEst(EventType eventType, String eventDateTime) {
        return getEventBuilder(eventType, eventDateTime)
                .eventClassifierCode(EventClassifierCode.EST)
                .build();
    }

    private static Event.EventBuilder<?, ?> getEventBuilder(EventType eventType, String eventDateTime) {
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

    private static References getRef1() {
        return References.newBuilder()
                .setReferenceType(RefTypeEnum.SI)
                .setReferenceValue("4351297464401500")
                .build();
    }
}
