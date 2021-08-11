package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maersk.jaxb.pojo.GEMSPubType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.DocumentReference;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mapping.MappingException;

import java.util.List;
import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.DocumentReference.Key.BKG;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.DocumentReference.Key.TRD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.INTE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.FacilityTypeCode.POTE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall.TransPortMode.VESSEL;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportEvent.TransportEventType.ARRI;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportEvent.TransportEventType.DEPA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_ARRIVALEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_DEPARTUREEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDemoEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRAIL_DEPARTUREEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETAEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransportEventMapperTest {

    private final static Event baseEventData = getEventForTransportEventType() ;
    private TransportEventMapperImpl transportEventMapper = new TransportEventMapperImpl();

    @ParameterizedTest
    @MethodSource("createTransportEventTestData")
    void testTransportEventDataForLegitScenarios(GEMSPubType gemsData, Event baseEvent, TransportEvent expectedTransportEvent) {
        var pubSetData = gemsData.getPubSet().get(0);
        var actualTransportEvent = transportEventMapper.fromSomethingToTransportEvent(pubSetData, baseEvent);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var str = objectMapper.writeValueAsString(actualTransportEvent);
            System.out.println(str);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        assertEquals(expectedTransportEvent, actualTransportEvent, "Transport Event does not match");
    }

    @Test
    void testTransportEventDataForBadData() {
        var pubSetData = getPubSetTypeWithDemoEventAct();
        assertThrows(MappingException.class, () -> transportEventMapper.fromSomethingToTransportEvent(pubSetData, baseEventData));
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
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithCONTAINER_ARRIVALEventAct())), baseEventData, getTransportEventTestData(ARRI, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithCONTAINER_DEPARTUREEventAct())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct())), baseEventData, getTransportEventTestData(ARRI, getTransportCall(INTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithRAIL_DEPARTUREEventAct())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(INTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETAEventAct())), baseEventData, getTransportEventTestData(ARRI, getTransportCall(POTE, VESSEL), getDocumentRef())),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_ETDEventAct())), baseEventData, getTransportEventTestData(DEPA, getTransportCall(POTE, VESSEL), getDocumentRef()))
                );
    }

    private static List<DocumentReference> getDocumentRef() {
        return List.of(getDocRefBkg(), getDocRefTrd());
    }

    private static DocumentReference getDocRefTrd() {
        return DocumentReference.builder()
                .key(TRD)
                .value("293156737")
                .build();
    }

    private static DocumentReference getDocRefBkg() {
        return DocumentReference.builder()
                .key(BKG)
                .value("209989099")
                .build();
    }

    private static TransportCall getTransportCall(TransportCall.FacilityTypeCode facilityCode, TransportCall.TransPortMode transPortMode) {
        return TransportCall.builder()
                .facilityTypeCode(facilityCode)
                .carrierServiceCode("LineCode")
                .carrierVoyageNumber("MUMMRSK")
                .otherFacility("Copenhagen")
                .modeOfTransport(transPortMode)
                .build();
    }


    private static TransportEvent getTransportEventTestData(TransportEvent.TransportEventType transportEventType, TransportCall transportCall, List<DocumentReference> documentReferences) {
        return TransportEvent.builder()
                .transportEventType(transportEventType)
                .transportCall(transportCall)
                .documentReferences(documentReferences)
                .eventID(baseEventData.getEventID())
                .bookingReference(baseEventData.getBookingReference())
                .eventDateTime(baseEventData.getEventDateTime())
                .eventType(baseEventData.getEventType())
                .eventCreatedDateTime(baseEventData.getEventCreatedDateTime())
                .eventClassifierCode(baseEventData.getEventClassifierCode())
                .parties(baseEventData.getParties())
                .references(baseEventData.getReferences())
                .equipmentReference(baseEventData.getEquipmentReference())
                .carrierBookingReference(baseEventData.getCarrierBookingReference())
                .transportDocumentReference(baseEventData.getTransportDocumentReference())
                .sourceSystem(baseEventData.getSourceSystem())
                .serviceType(baseEventData.getServiceType())
                .carrierCode(baseEventData.getCarrierCode())
                .build();
    }
}
