package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import MSK.com.external.dcsa.ShipmentEvent;
import MSK.com.external.dcsa.ShipmentEventType;
import MSK.com.external.dcsa.ShipmentInformationType;
import MSK.com.gems.GEMSPubType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DocumentIdMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ShipmentEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ShipmentInformationTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.ShipmentEventType.CONF;
import static MSK.com.external.dcsa.ShipmentEventType.ISSU;
import static MSK.com.external.dcsa.ShipmentEventType.PENA;
import static MSK.com.external.dcsa.ShipmentEventType.RECE;
import static MSK.com.external.dcsa.ShipmentEventType.REJE;
import static MSK.com.external.dcsa.ShipmentEventType.SURR;
import static MSK.com.external.dcsa.ShipmentInformationType.ARN;
import static MSK.com.external.dcsa.ShipmentInformationType.BOK;
import static MSK.com.external.dcsa.ShipmentInformationType.SHI;
import static MSK.com.external.dcsa.ShipmentInformationType.SRM;
import static MSK.com.external.dcsa.ShipmentInformationType.TRD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVAL_NOTICEEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithArrange_Cargo_Release_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithArrange_Cargo_Release_OpenEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithConfirm_Shipment_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDemoEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithIssue_Original_TPDOC_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRELEASEEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithReceive_Transport_Document_Instructions_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_CancelledEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForShipmentEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ShipmentEventMapperImpl.class,
        ShipmentEventTypeMapper.class,
        ShipmentInformationTypeMapper.class,
        DocumentIdMapper.class})
class ShipmentEventMapperTest {

    private final static Event baseEventData = getEventForShipmentEventType();

    @Autowired
    ShipmentEventMapper shipmentEventMapper;

    @ParameterizedTest
    @MethodSource("createShipmentEventTestData")
    void testShipmentEventDataForLegitScenarios(GEMSPubType gemsData, Event baseEvent, ShipmentEvent expectedShipmentEvent) {
        var pubSetData = gemsData.getPubSet().get(0);
        var actualShipmentEvent = shipmentEventMapper.fromPubSetTypeToShipmentEvent(pubSetData, baseEvent);
        assertEquals(expectedShipmentEvent, actualShipmentEvent, "Shipment Event does not match");
    }

    @Test
    void testShipmentEventDataForBadData() {
        var pubSetData = getPubSetTypeWithDemoEventAct();
        assertThrows(MappingException.class, () -> shipmentEventMapper.fromPubSetTypeToShipmentEvent(pubSetData, baseEventData));
    }


    protected static Stream<Arguments> createShipmentEventTestData() {
        return Stream.of(Arguments.arguments(getGemsData(List.of(getPubSetTypeWithArrange_Cargo_Release_ClosedEventAct())), baseEventData, getShipmentEventTestData("293156737", CONF, SRM)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithConfirm_Shipment_ClosedEventAct())), baseEventData, getShipmentEventTestData("209989099", CONF, BOK)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithArrange_Cargo_Release_OpenEventAct())), baseEventData, getShipmentEventTestData("293156737", PENA, SRM)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithARRIVAL_NOTICEEventAct())), baseEventData, getShipmentEventTestData("293156737", ISSU, ARN)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithIssue_Original_TPDOC_ClosedEventAct())), baseEventData, getShipmentEventTestData("293156737", ISSU, TRD)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithReceive_Transport_Document_Instructions_ClosedEventAct())), baseEventData, getShipmentEventTestData("293156737", RECE, SHI)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithRELEASEEventAct())), baseEventData, getShipmentEventTestData("293156737", SURR, TRD)),
                Arguments.arguments(getGemsData(List.of(getPubSetTypeWithShipment_CancelledEventAct())), baseEventData, getShipmentEventTestData("209989099", REJE, BOK))
                );
    }


    private static ShipmentEvent getShipmentEventTestData(String documentID, ShipmentEventType shipmentEventType, ShipmentInformationType informationTypeCode) {
        var shipmentEvent = new ShipmentEvent();
        shipmentEvent.setShipmentInformationType(informationTypeCode);
        shipmentEvent.setShipmentEventType(shipmentEventType);
        shipmentEvent.setDocumentID(documentID);
        shipmentEvent.setEventID(baseEventData.getEventID());
        shipmentEvent.setBookingReference(baseEventData.getBookingReference());
        shipmentEvent.setEventDateTime(baseEventData.getEventDateTime());
        shipmentEvent.setEventType(baseEventData.getEventType());
        shipmentEvent.setEventCreatedDateTime(baseEventData.getEventCreatedDateTime());
        shipmentEvent.setEventClassifierCode(baseEventData.getEventClassifierCode());
        shipmentEvent.setParties(baseEventData.getParties());
        shipmentEvent.setReferences(baseEventData.getReferences());
        shipmentEvent.setEquipmentReference(baseEventData.getEquipmentReference());
        shipmentEvent.setCarrierBookingReference(baseEventData.getCarrierBookingReference());
        shipmentEvent.setTransportDocumentReference(baseEventData.getTransportDocumentReference());
        shipmentEvent.setSourceSystem(baseEventData.getSourceSystem());
        shipmentEvent.setServiceType(baseEventData.getServiceType());
        shipmentEvent.setCarrierCode(baseEventData.getCarrierCode());
        return shipmentEvent;
    }
}
