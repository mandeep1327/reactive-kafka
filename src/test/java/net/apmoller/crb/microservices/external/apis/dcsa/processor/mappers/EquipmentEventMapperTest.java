package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import MSK.com.external.dcsa.DocumentReference;
import MSK.com.external.dcsa.EmptyIndicatorCode;
import MSK.com.external.dcsa.EquipmentEvent;
import MSK.com.external.dcsa.EquipmentEventType;
import MSK.com.external.dcsa.SealSource;
import MSK.com.external.dcsa.Seals;
import MSK.com.external.dcsa.TransPortMode;
import MSK.com.external.dcsa.TransportCall;
import MSK.com.gems.GEMSPubType;
import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.EquipmentEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapperImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.DocumentReferenceType.BKG;
import static MSK.com.external.dcsa.DocumentReferenceType.TRD;
import static MSK.com.external.dcsa.EquipmentEventType.DISC;
import static MSK.com.external.dcsa.EquipmentEventType.GTIN;
import static MSK.com.external.dcsa.EquipmentEventType.GTOT;
import static MSK.com.external.dcsa.EquipmentEventType.LOAD;
import static MSK.com.external.dcsa.EquipmentEventType.STUF;
import static MSK.com.external.dcsa.TransPortMode.RAIL;
import static MSK.com.external.dcsa.TransPortMode.TRUCK;
import static MSK.com.external.dcsa.TransPortMode.VESSEL;
import static MSK.com.external.dcsa.TransPortMode.BARGE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDISCHARGE_NEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDemoEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_IN_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithBarGATE_IN_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_OUTEXPYEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_OUTEXPYEventActAndSeals;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithLOAD_NEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithOFF_RAILIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithON_RAIL_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithSTUFFINGEXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_CancelledEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithoutTransportPlan;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithoutVesselData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForEquipmentEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EquipmentEventMapperImpl.class, EquipmentEventTypeMapper.class})
class EquipmentEventMapperTest {

    private final static Event baseEventData = getEventForEquipmentEventType();

    @Autowired
    EquipmentEventMapper equipmentEventMapper;

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
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithOFF_RAILIMPNEventAct(null))), getEquipmentEventTestData(GTIN, getTransportCall(RAIL))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithDISCHARGE_NEventAct(null))), getEquipmentEventTestData(DISC, getTransportCall(VESSEL))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithGATE_IN_EXPNEventAct(null))), getEquipmentEventTestData(GTIN, getTransportCallForKolkata(TRUCK))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithSTUFFINGEXPNEventAct(null))), getEquipmentEventTestData(STUF, getTransportCallForKolkata(TRUCK))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithON_RAIL_EXPNEventAct(null))), getEquipmentEventTestData(GTOT, getTransportCallForKolkata(RAIL))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithGATE_OUTEXPYEventAct(null))), getEquipmentEventTestData(GTOT, getTransportCallForKolkata(TRUCK))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithGATE_OUTEXPYEventActAndSeals())), getEquipmentEventTestDataWithSeals(GTOT, getTransportCallForKolkata(TRUCK))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithBarGATE_IN_EXPNEventAct(null))), getEquipmentEventTestData(GTIN, getTransportCallWithoutVoyageForKolkata(BARGE))),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithLOAD_NEventAct(null))), getEquipmentEventTestData(LOAD, getTransportCallForKolkata(VESSEL)))
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

    private static TransportCall getTransportCall(TransPortMode mode) {

        var transportCall = new TransportCall();
        transportCall.setCarrierServiceCode("LineCode");
        transportCall.setOtherFacility("Copenhagen");
        transportCall.setCarrierVoyageNumber("MRSK1235");
        transportCall.setModeOfTransport(mode);

        return transportCall;
    }

    private static TransportCall getTransportCallForKolkata(TransPortMode mode ) {

        var transportCall = new TransportCall();
        transportCall.setCarrierServiceCode("LineCode");
        transportCall.setCarrierVoyageNumber("MRSK1235");
        transportCall.setOtherFacility("Kolkata");
        transportCall.setModeOfTransport(mode);

        return transportCall;
    }
    private static TransportCall getTransportCallWithoutVoyageForKolkata(TransPortMode mode ) {

        var transportCall = new TransportCall();
        transportCall.setCarrierServiceCode("LineCode");
        transportCall.setCarrierVoyageNumber(null);
        transportCall.setOtherFacility("Kolkata");
        transportCall.setModeOfTransport(mode);

        return transportCall;
    }



    private static EquipmentEvent getEquipmentEventTestData (EquipmentEventType eventType, TransportCall transportCall) {
         var equipmentEvent = new EquipmentEvent();
         equipmentEvent.setEquipmentEventTypeCode(eventType);
         equipmentEvent.setEmptyIndicatorCode(EmptyIndicatorCode.LADEN);
         equipmentEvent.setDocumentReferences(getDocumentRef());
         equipmentEvent.setSeals(new ArrayList<>());
         equipmentEvent.setEventID(baseEventData.getEventID());
         equipmentEvent.setTransportCall(transportCall);
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

    private static EquipmentEvent getEquipmentEventTestDataWithSeals (EquipmentEventType eventType, TransportCall transportCall) {
         var equipmentEvent = getEquipmentEventTestData(eventType, transportCall);
         equipmentEvent.setSeals(getSealInformation());
        return equipmentEvent;
    }

    private static List<Seals> getSealInformation() {
        return List.of(new Seals("MaerskSeal", SealSource.CAR),
                new Seals("ShipperValue", SealSource.SHI),
                new Seals("VetValue", SealSource.VET),
                new Seals("CustomsValue", SealSource.CUS));
    }

    private static List<DocumentReference> getDocumentRef() {
        return List.of(getDocRefBkg(), getDocRefTrd());
    }

    private static DocumentReference getDocRefTrd() {
        var docRef = new DocumentReference();
        docRef.setDocumentReferenceType(TRD);
        docRef.setDocumentReferenceValue("293156737");
        return docRef;
    }

    private static DocumentReference getDocRefBkg() {
        var docRef = new DocumentReference();
        docRef.setDocumentReferenceType(BKG);
        docRef.setDocumentReferenceValue("209989099");
        return docRef;
    }
}
