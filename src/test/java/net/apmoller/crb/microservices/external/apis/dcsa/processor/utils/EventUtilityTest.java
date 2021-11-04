package net.apmoller.crb.microservices.external.apis.dcsa.processor.utils;

import MSK.com.external.dcsa.TransportEventType;
import MSK.com.gems.EquipmentType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.TransportEventType.*;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.EquipmentTestDataBuilder.getEquipmentList;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVECUIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getValidTransportPlan;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.*;
import static org.junit.jupiter.api.Assertions.*;

class EventUtilityTest {

    @Test
    void testTimeStampInISOFormat(){
        assertAll(
                () -> assertNotNull(EventUtility.getTimeStampInUTCFormat("2021-12-12 12:23:59")),
                () -> assertThrows(DateTimeParseException.class, () -> EventUtility.getTimeStampInUTCFormat("2021-12-12 12:2")),
                () -> assertNull(EventUtility.getTimeStampInUTCFormat(null)),
                () -> assertEquals("2021-12-12T12:23:59Z" ,EventUtility.getTimeStampInUTCFormat("2021-12-12 12:23:59"))
        );
    }

    @Test
    void testShipmentEvents(){
        assertAll(
                () -> assertNotEquals(0, SHIPMENT_EVENTS.size()),
                () -> assertFalse(SHIPMENT_EVENTS.isEmpty()),
                () -> assertEquals(9, SHIPMENT_EVENTS.size())
        );
    }

    @Test
    void testTransportEvents(){
        assertAll(
                () -> assertNotEquals(0, TRANSPORT_EVENTS.size()),
                () -> assertFalse(TRANSPORT_EVENTS.isEmpty()),
                () -> assertEquals(8, TRANSPORT_EVENTS.size())
        );
    }

    @Test
    void testEstEvents(){
        assertAll(
                () -> assertNotEquals(0, EST_EVENTS.size()),
                () -> assertFalse(EST_EVENTS.isEmpty()),
                () -> assertEquals(2, EST_EVENTS.size())
        );
    }

    @Test
    void testActEvents(){
        assertAll(
                () -> assertNotEquals(0, ACT_EVENTS.size()),
                () -> assertFalse(ACT_EVENTS.isEmpty()),
                () -> assertEquals(25, ACT_EVENTS.size())
        );
    }

    @ParameterizedTest
    @MethodSource("createEventTypes")
    void testArrivalOrDepartureEventType(String eventType, TransportEventType transportEventType){
        assertEquals(transportEventType, EventUtility.getArrivalOrDepartureEventType(eventType));
    }

    static Stream<Arguments> createEventTypes() {
        return Stream.of(
                Arguments.of("ARRIVECUIMPN", ARRI),
                Arguments.of("CONTAINER ARRIVAL", ARRI),
                Arguments.of("RAIL_ARRIVAL_AT_DESTINATION", ARRI),
                Arguments.of("Shipment_ETA", ARRI),
                Arguments.of("CONTAINER DEPARTURE", DEPA),
                Arguments.of("RAIL_DEPARTURE", DEPA),
                Arguments.of("DEPARTCUEXPN", DEPA),
                Arguments.of("Shipment_ETD", DEPA)
                );
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("createBadEventTypes")
    void testArrivalOrDepartureEventType(String eventType){
        assertThrows(MappingException.class, () ->  EventUtility.getArrivalOrDepartureEventType(eventType));
    }

    static Stream<Arguments> createBadEventTypes() {
        return Stream.of(
                Arguments.of("hjsdgfc"),
                Arguments.of("RAIL_ARRIVAL_AT DESTINATION")
        );
    }

    @ParameterizedTest
    @MethodSource("createEquipmentEventTypes")
    void testArrivalOrDepartureEquipmentEvent(String eventType, TransportEventType transportEventType){
        assertEquals(transportEventType, EventUtility.getArrivalOrDepartureEquipmentEvent(eventType));
    }

    static Stream<Arguments> createEquipmentEventTypes() {
        return Stream.of(
                Arguments.of("DISCHARG   N", ARRI),
                Arguments.of("OFF-RAILIMPN", ARRI),
                Arguments.of("GATE-IN EXPN", DEPA),
                Arguments.of("GATE-OUTEXPY", DEPA),
                Arguments.of("LOAD       N", DEPA),
                Arguments.of("ON-RAIL EXPN", DEPA),
                Arguments.of("STUFFINGEXPN", DEPA),
                Arguments.of("", OMIT),
                Arguments.of("DISCHARG N", OMIT),
                Arguments.of("dghftghj", OMIT)
                );
    }

    @ParameterizedTest
    @NullSource
    void testArrivalOrDepartureEquipmentEvent(String eventType){
        assertThrows(MappingException.class, () ->  EventUtility.getArrivalOrDepartureEquipmentEvent(eventType));
    }

    @Test
    void testTPEventTypeFromPubSetType(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals(ARRI, EventUtility.getTPEventTypeFromPubSetType(pubSetType));
    }

    @Test
    void testTPEventTypeFromPubSetTypeWithEmptyEventType(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        pubSetType.setEvent(null);
        assertThrows(MappingException.class,() -> EventUtility.getTPEventTypeFromPubSetType(pubSetType));
        assertThrows(MappingException.class,() -> EventUtility.getTPEventTypeFromPubSetType(null));
    }

    @Test
    void testFirstEquipmentElementWithNullEquipmentType(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals(getEquipmentList().get(0),EventUtility.getFirstEquipmentElement(pubSetType));
    }

    @Test
    void testFirstEquipmentElement(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals(getEquipmentList().get(0),EventUtility.getFirstEquipmentElement(pubSetType));
        assertEquals(new EquipmentType(),EventUtility.getFirstEquipmentElement(null));
    }

    @Test
    void testSourceSystemFromPubsetType(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals("GCSS",EventUtility.getSourceSystemFromPubsetType(pubSetType));
    }

    @Test
    void testLastTransportPlanWithPortOfDischarge(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals(Optional.of(getValidTransportPlan()),EventUtility.getLastTransportPlanWithPortOfDischarge(pubSetType));
    }

    @Test
    void testFirstTransportPlanTypeWithPortOfLoad(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals(Optional.of(getValidTransportPlan()),EventUtility.getFirstTransportPlanTypeWithPortOfLoad(pubSetType));
    }

    @Test
    void testBookingNumber(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals("209989099",EventUtility.getBookingNumber(pubSetType));
    }

    @Test
    void testBolNumber(){
        var pubSetType = getPubSetTypeWithARRIVECUIMPNEventAct();
        assertEquals("293156737",EventUtility.getBolNumber(pubSetType));
    }

}
