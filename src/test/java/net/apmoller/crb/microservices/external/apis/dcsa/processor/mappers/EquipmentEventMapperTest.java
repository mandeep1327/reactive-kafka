package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import MSK.com.external.dcsa.DocumentReference;
import MSK.com.external.dcsa.EmptyIndicatorCode;
import MSK.com.external.dcsa.EquipmentEvent;
import MSK.com.external.dcsa.EquipmentEventType;
import MSK.com.external.dcsa.FacilityType;
import MSK.com.external.dcsa.TransportCall;
import MSK.com.gems.GEMSPubType;
import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mapping.MappingException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.EquipmentEventType.GTIN;
import static MSK.com.external.dcsa.EquipmentEventType.GTOT;
import static MSK.com.external.dcsa.EquipmentEventType.LOAD;
import static MSK.com.external.dcsa.FacilityType.CLOC;
import static MSK.com.external.dcsa.FacilityType.DEPO;
import static MSK.com.external.dcsa.FacilityType.INTE;
import static MSK.com.external.dcsa.FacilityType.POTE;
import static MSK.com.external.dcsa.Key.BKG;
import static MSK.com.external.dcsa.Key.TRD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVECUIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDEPARTCUEXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDemoEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_IN_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_OUTEXPYEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithLOAD_NEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithOFF_RAILIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithON_RAIL_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_CancelledEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithoutTransportPlan;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithoutVesselData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForEquipmentEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EquipmentEventMapperTest {

    private final static Event baseEventData = getEventForEquipmentEventType();
    private EquipmentEventMapperImpl equipmentEventMapper = new EquipmentEventMapperImpl();

    @ParameterizedTest
    @MethodSource("createEquipmentEventTestData")
    void testEquipmentDataForLegitScenarios(GEMSPubType gemsData, EquipmentEvent expectedEquipmentEvent) {
        var pubSetData = gemsData.getPubSet().get(0);
        var actualEquipmentEvent = equipmentEventMapper.fromPubSetToEquipmentEvent(pubSetData, baseEventData);
        assertEquals(expectedEquipmentEvent, actualEquipmentEvent, "Equipment Event does not match");
    }

    @ParameterizedTest
    @MethodSource("createEquipmentEventBadTestData")
    void testEquipmentEventDataForBadData(String expectedExceptionMessage, PubSetType pubSetData) {
        var exception = assertThrows(MappingException.class, () -> equipmentEventMapper.fromPubSetToEquipmentEvent(pubSetData, baseEventData));
        assertEquals(expectedExceptionMessage, exception.getMessage(), "Exception thrown badly");
    }

    private static Stream<Arguments> createEquipmentEventTestData() {
        return Stream.of(
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithARRIVECUIMPNEventAct())), getEquipmentEventTestData(GTIN, CLOC)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithGATE_IN_EXPNEventAct())), getEquipmentEventTestData(GTIN, POTE)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithOFF_RAILIMPNEventAct())), getEquipmentEventTestData(GTIN, INTE)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithDEPARTCUEXPNEventAct())), getEquipmentEventTestData(GTOT, CLOC)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithON_RAIL_EXPNEventAct())), getEquipmentEventTestData(GTOT, INTE)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithGATE_OUTEXPYEventAct())), getEquipmentEventTestData(GTOT, DEPO)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithLOAD_NEventAct())), getEquipmentEventTestData(LOAD, POTE))
        );
    }

    private static Stream<Arguments> createEquipmentEventBadTestData() {
        return Stream.of(
                //test invalid event
                Arguments.arguments("Could not map Equipment Event Type of NA_Event_Act", getPubSetTypeWithDemoEventAct()),
                //test shipment event
                Arguments.arguments("Could not map Equipment Event Type of Shipment_Cancelled", getPubSetTypeWithShipment_CancelledEventAct()),
                //test with payload having no transport plan
                Arguments.arguments("Could not map Equipment Event Type of Shipment_ETD", getPubSetTypeWithoutTransportPlan()),
                //test  payload having no vessel data
                Arguments.arguments("Could not map Equipment Event Type of Shipment_ETD", getPubSetTypeWithoutVesselData())
        );
    }

    private static TransportCall getTransportCall(FacilityType facilityCode) {

        var transportCall = new TransportCall();
        transportCall.setFacilityType(facilityCode);
        transportCall.setCarrierServiceCode("LineCode");
        transportCall.setOtherFacility("Copenhagen");

        return transportCall;
    }



    private static EquipmentEvent getEquipmentEventTestData (EquipmentEventType eventType, FacilityType facilityTypeCode) {
         var equipmentEvent = new EquipmentEvent();
         equipmentEvent.setEquipmentEventType(eventType);
         equipmentEvent.setEmptyIndicatorCode(EmptyIndicatorCode.LADEN);
         equipmentEvent.setDocumentReferences(getDocumentRef());
         equipmentEvent.setSeals(new ArrayList<>());
         equipmentEvent.setEventID(baseEventData.getEventID());
         equipmentEvent.setTransportCall(getTransportCall(facilityTypeCode));
         equipmentEvent.setBookingReference(baseEventData.getBookingReference());
         equipmentEvent.setEventDateTime(baseEventData.getEventDateTime());
         equipmentEvent.setEventType(baseEventData.getEventType());
         equipmentEvent.setEventCreatedDateTime(baseEventData.getEventCreatedDateTime());
         equipmentEvent.setEventClassifierCode(baseEventData.getEventClassifierCode());
         equipmentEvent.setParties(baseEventData.getParties());
         equipmentEvent.setReferences(baseEventData.getReferences());
         equipmentEvent.setEquipmentReference(baseEventData.getEquipmentReference());
         equipmentEvent.setCarrierBookingReference(baseEventData.getCarrierBookingReference());
         equipmentEvent.setTransportDocumentReference(baseEventData.getTransportDocumentReference());
         equipmentEvent.setSourceSystem(baseEventData.getSourceSystem());
         equipmentEvent.setServiceType(baseEventData.getServiceType());
         equipmentEvent.setCarrierCode(baseEventData.getCarrierCode());

        return equipmentEvent;
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
}
