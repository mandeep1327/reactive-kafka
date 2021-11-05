package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DCSAEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapperImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVAL_NOTICEEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithArrange_Cargo_Release_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithArrange_Cargo_Release_OpenEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_ARRIVALEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_DEPARTUREEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithConfirm_Shipment_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithDISCHARG_NEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_IN_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithGATE_OUTEXPYEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithIssue_Original_TPDOC_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithLOAD_NEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithOFF_RAILIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithON_RAIL_EXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRAIL_DEPARTUREEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithRELEASEEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithReceive_Transport_Document_Instructions_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithSTRIPPIN_YEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithSTUFFINGEXPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_CancelledEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETAEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETAEventEst;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETAEventEstWithGCSSExpArrival;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETAEventEstWithNoTime;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventEst;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventEstWithGCSSExpDeparture;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithShipment_ETDEventEstWithNoTime;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.ShipmentTestDataBuilder.getPartyList;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForEquipmentEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForShipmentEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventACT;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventTypeWithESTEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventTypeWithNoEventDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EventMapperImpl.class, DCSAEventTypeMapper.class})
class EventMapperTest {

    @Autowired
    private EventMapper eventMapper;

    @ParameterizedTest
    @MethodSource("testDataForShipmentEventActs")
    void testEventDataForShipmentEvent(PubSetType pubSetData, Event eventData){
        var actual = eventMapper.fromPubSetTypeToEvent(pubSetData);
        assertEquals(eventData, actual, "Event does not match for Shipment Events");
    }

    private static Stream<Arguments> testDataForShipmentEventActs() {
        return Stream.of(
                Arguments.arguments(getPubSetTypeWithArrange_Cargo_Release_ClosedEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithArrange_Cargo_Release_OpenEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithARRIVAL_NOTICEEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithConfirm_Shipment_ClosedEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithIssue_Original_TPDOC_ClosedEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithRELEASEEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithReceive_Transport_Document_Instructions_ClosedEventAct(), getEventForShipmentEventType()),
                Arguments.arguments(getPubSetTypeWithShipment_CancelledEventAct(), getEventForShipmentEventType())
        );
    }

    @ParameterizedTest
    @MethodSource("testDataForEquipmentEventActs")
    void testEventDataForEquipmentEvent(PubSetType pubSetData, Event eventData){
        var actual = eventMapper.fromPubSetTypeToEvent(pubSetData);
        assertEquals(eventData, actual, "Event does not match for Equipment Events");
    }

    private static Stream<Arguments> testDataForEquipmentEventActs() {
        return Stream.of(
                Arguments.arguments(getPubSetTypeWithDISCHARG_NEventAct(), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithGATE_IN_EXPNEventAct(getPartyList()), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithGATE_OUTEXPYEventAct(getPartyList()), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithLOAD_NEventAct(getPartyList()), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithOFF_RAILIMPNEventAct(getPartyList()), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithON_RAIL_EXPNEventAct(getPartyList()), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithSTRIPPIN_YEventAct(getPartyList()), getEventForEquipmentEventType()),
                Arguments.arguments(getPubSetTypeWithSTUFFINGEXPNEventAct(getPartyList()), getEventForEquipmentEventType())
        );
    }



    @ParameterizedTest
    @MethodSource("testEventDataForTransportEventActs")
    void testEventDataForTransportEvent(PubSetType pubSetData, Event eventData){
        var actual = eventMapper.fromPubSetTypeToEvent(pubSetData);
        assertEquals(eventData, actual, "Event does not match for Transport Events");
    }

    private static Stream<Arguments> testEventDataForTransportEventActs() {
        return Stream.of(
                Arguments.arguments(getPubSetTypeWithCONTAINER_ARRIVALEventAct(), getEventForTransportEventType()),
                Arguments.arguments(getPubSetTypeWithCONTAINER_DEPARTUREEventAct(), getEventForTransportEventType()),
                Arguments.arguments(getPubSetTypeWithRAIL_ARRIVAL_AT_DESTINATIONEventAct(), getEventForTransportEventType()),
                Arguments.arguments(getPubSetTypeWithShipment_ETAEventAct(), getEventForTransportEventACT()),
                Arguments.arguments(getPubSetTypeWithShipment_ETDEventAct(), getEventForTransportEventACT()),
                Arguments.arguments(getPubSetTypeWithShipment_ETAEventEst(), getEventForTransportEventTypeWithESTEventAct()),
                Arguments.arguments(getPubSetTypeWithShipment_ETDEventEst(), getEventForTransportEventTypeWithESTEventAct()),
                Arguments.arguments(getPubSetTypeWithShipment_ETAEventEstWithGCSSExpArrival(), getEventForTransportEventTypeWithESTEventAct()),
                Arguments.arguments(getPubSetTypeWithShipment_ETDEventEstWithGCSSExpDeparture(), getEventForTransportEventTypeWithESTEventAct()),
                Arguments.arguments(getPubSetTypeWithShipment_ETAEventEstWithNoTime(), getEventForTransportEventTypeWithNoEventDateTime()),
                Arguments.arguments(getPubSetTypeWithShipment_ETDEventEstWithNoTime(), getEventForTransportEventTypeWithNoEventDateTime()),
                Arguments.arguments(getPubSetTypeWithRAIL_DEPARTUREEventAct(), getEventForTransportEventType())
        );
    }
}
