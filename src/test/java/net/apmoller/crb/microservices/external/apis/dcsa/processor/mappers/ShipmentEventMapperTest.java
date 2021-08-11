package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maersk.jaxb.pojo.GEMSPubType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.mapping.MappingException;

import java.util.List;
import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.CONF;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.ISSU;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.PENA;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.RECE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.REJE;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentEventType.SURR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.ARN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.BOK;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.SHI;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.SRM;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.TRD;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ShipmentEvent.ShipmentInformationTypeCode.VGM;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVAL_NOTICEEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithArrange_Cargo_Release_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithArrange_Cargo_Release_OpenEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithConfirm_Shipment_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDemoEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithEquipment_VGM_Details_UpdatedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithIssue_Original_TPDOC_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRELEASEEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithReceive_Transport_Document_Instructions_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_CancelledEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForShipmentEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShipmentEventMapperTest {

    private final static Event baseEventData = getEventForShipmentEventType();
    private ShipmentEventMapperImpl shipmentEventMapper = new ShipmentEventMapperImpl();

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


    private static ShipmentEvent getShipmentEventTestData(String documentID, ShipmentEvent.ShipmentEventType shipmentEventType, ShipmentEvent.ShipmentInformationTypeCode informationTypeCode) {
        return ShipmentEvent.builder()
                .documentTypeCode(informationTypeCode)
                .shipmentEventTypeCode(shipmentEventType)
                .documentID(documentID)
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
