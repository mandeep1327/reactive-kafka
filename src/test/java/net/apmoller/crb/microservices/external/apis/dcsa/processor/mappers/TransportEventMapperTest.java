package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import MSK.com.external.dcsa.DocumentReference;
import MSK.com.external.dcsa.FacilityType;
import MSK.com.external.dcsa.TransPortMode;
import MSK.com.external.dcsa.TransportCall;
import MSK.com.external.dcsa.TransportEvent;
import MSK.com.external.dcsa.TransportEventType;
import com.maersk.jaxb.pojo.GEMSPubType;
import com.maersk.jaxb.pojo.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mapping.MappingException;

import java.util.List;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.FacilityType.INTE;
import static MSK.com.external.dcsa.FacilityType.POTE;
import static MSK.com.external.dcsa.Key.BKG;
import static MSK.com.external.dcsa.Key.TRD;
import static MSK.com.external.dcsa.TransPortMode.BARGE;
import static MSK.com.external.dcsa.TransPortMode.RAIL;
import static MSK.com.external.dcsa.TransPortMode.TRUCK;
import static MSK.com.external.dcsa.TransPortMode.VESSEL;
import static MSK.com.external.dcsa.TransportEventType.ARRI;
import static MSK.com.external.dcsa.TransportEventType.DEPA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_ARRIVALEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_DEPARTUREEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDemoEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithOFF_RAILIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRAIL_DEPARTUREEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_CancelledEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETAEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventActAndBARTransportMode;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventActAndBCOTransportMode;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventActAndFEOTransportMode;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventActAndRCOTransportMode;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventActAndTRKTransportMode;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventActAndVSMTransportMode;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithoutTransportPlan;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithoutVesselData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransportEventMapperTest {

    private final static Event baseEventData = getEventForTransportEventType() ;
    private final TransportEventMapperImpl transportEventMapper = new TransportEventMapperImpl();

    @ParameterizedTest
    @MethodSource("createTransportEventTestData")
    void testTransportEventDataForLegitScenarios(GEMSPubType gemsData, Event baseEvent, TransportEvent expectedTransportEvent) {
        var pubSetData = gemsData.getPubSet().get(0);
        var actualTransportEvent = transportEventMapper.fromPubSetToTransportEvent(pubSetData, baseEvent);
        assertEquals(expectedTransportEvent, actualTransportEvent, "Transport Event does not match");
    }

    @ParameterizedTest
    @MethodSource("createTransportEventBadTestData")
    void testTransportEventDataForBadData(String expectedExceptionMessage, PubSetType pubSetData) {
        var exception = assertThrows(MappingException.class, () -> transportEventMapper.fromPubSetToTransportEvent(pubSetData, baseEventData));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception thrown badly");

    }

    private static Stream<Arguments> createTransportEventBadTestData(){
        return Stream.of(
                //test invalid event
                Arguments.arguments("Could not map Transport Event Type of NA_Event_Act", getPubSetTypeWithDemoEventAct()),
                //test shipment event
                Arguments.arguments("Could not map Transport Event Type of Shipment_Cancelled", getPubSetTypeWithShipment_CancelledEventAct()),
                //test equipment event
                Arguments.arguments("Could not map Transport Event Type of OFF-RAILIMPN", getPubSetTypeWithOFF_RAILIMPNEventAct()),
                //test with payload having no transport plan
                Arguments.arguments("TransportPlan can not be empty for the Transport Event", getPubSetTypeWithoutTransportPlan()),
                 //test  payload having no vessel data
                Arguments.arguments("Could not find the vessel code", getPubSetTypeWithoutVesselData())
        );
    }


    protected static Stream<Arguments> createTransportEventTestData() {
        return Stream.of(
                //checking facilty type codes and transport event types
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithCONTAINER_ARRIVALEventAct())), baseEventData, getTransportEventTestData(ARRI, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithCONTAINER_DEPARTUREEventAct())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct())), baseEventData, getTransportEventTestData(ARRI, getTransportCall(INTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithRAIL_DEPARTUREEventAct())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(INTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETAEventAct())), baseEventData, getTransportEventTestData(ARRI, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventAct())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, VESSEL), getDocumentRef())),
                //checking transport modes
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventActAndBARTransportMode())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, BARGE), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventActAndBCOTransportMode())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, BARGE), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventActAndFEOTransportMode())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventActAndVSMTransportMode())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventActAndTRKTransportMode())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, TRUCK), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventActAndRCOTransportMode())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, RAIL), getDocumentRef())));
    }

    private static List<DocumentReference> getDocumentRef() {
        return List.of(getDocRefBkg(), getDocRefTrd());
    }

    private static DocumentReference getDocRefTrd() {
        var docRef = new DocumentReference();
        docRef.setKey(TRD);
        docRef.setValue("293156737");
        return docRef;
    }

    private static DocumentReference getDocRefBkg() {
        var docRef = new DocumentReference();
        docRef.setKey(BKG);
        docRef.setValue("209989099");
        return docRef;
    }

    private static TransportCall getTransportCall(FacilityType facilityCode, TransPortMode transPortMode) {
        var transportCall = new TransportCall();

        transportCall.setFacilityType(facilityCode);
        transportCall.setCarrierServiceCode("LineCode");
        transportCall.setCarrierVoyageNumber("MUMMRSK");
        transportCall.setOtherFacility("Copenhagen");
        transportCall.setModeOfTransport(transPortMode);

        return transportCall;
    }


    private static TransportEvent getTransportEventTestData(TransportEventType transportEventType, TransportCall transportCall, List<DocumentReference> documentReferences) {

        var transportEvent = new TransportEvent();
        transportEvent.setTransportEventType(transportEventType);
        transportEvent.setTransportCall(transportCall);
        transportEvent.setDocumentReferences(documentReferences);
        transportEvent.setEventID(baseEventData.getEventID());
        transportEvent.setBookingReference(baseEventData.getBookingReference());
        transportEvent.setEventDateTime(baseEventData.getEventDateTime());
        transportEvent.setEventType(baseEventData.getEventType());
        transportEvent.setEventCreatedDateTime(baseEventData.getEventCreatedDateTime());
        transportEvent.setEventClassifierCode(baseEventData.getEventClassifierCode());
        transportEvent.setParties(baseEventData.getParties());
        transportEvent.setReferences(baseEventData.getReferences());
        transportEvent.setEquipmentReference(baseEventData.getEquipmentReference());
        transportEvent.setCarrierBookingReference(baseEventData.getCarrierBookingReference());
        transportEvent.setTransportDocumentReference(baseEventData.getTransportDocumentReference());
        transportEvent.setSourceSystem(baseEventData.getSourceSystem());
        transportEvent.setServiceType(baseEventData.getServiceType());
        transportEvent.setCarrierCode(baseEventData.getCarrierCode());
        return transportEvent;

    }
}
