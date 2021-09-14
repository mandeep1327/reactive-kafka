package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;


import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import MSK.com.external.dcsa.EquipmentEvent;
import MSK.com.external.dcsa.EventType;
import MSK.com.external.dcsa.ShipmentEvent;
import MSK.com.external.dcsa.TransportEvent;
import MSK.com.gems.GEMSPubType;
import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.exceptions.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DCSAEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;

import java.util.Collections;
import java.util.List;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithARRIVECUIMPNEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithCONTAINER_ARRIVALEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getPubSetTypeWithConfirm_Shipment_ClosedEventAct;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForEquipmentEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForShipmentEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForTransportEventType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EventDelegatorTest {

    private final ShipmentEventMapper shipmentMapper = mock(ShipmentEventMapper.class);
    private final EquipmentEventMapper equipmentMapper = mock(EquipmentEventMapper.class);
    private final TransportEventMapper transportMapper = mock(TransportEventMapper.class);
    private final EventMapper eventMapper = mock(EventMapper.class);
    private final DCSAEventTypeMapper eventTypeMapper = mock(DCSAEventTypeMapper.class);
    private final KafkaSender<String, DcsaTrackTraceEvent> kafkaSender = mock(KafkaSender.class);
    private final MetricsService metricsService = mock(MetricsService.class);


    private final EventDelegator eventDelegator = new EventDelegator( eventMapper, shipmentMapper, equipmentMapper, transportMapper, eventTypeMapper,kafkaSender,metricsService);

    @BeforeEach
    void setUpKafka(){
        ReflectionTestUtils.setField(eventDelegator,"kafkaPublisherTopic", "dummy");
        given(kafkaSender.send(any())).willReturn(Flux.empty());
    }

    @Test
    void testWithBlankGemsPayload(){
        var emptyGemsPubType = new GEMSPubType();
        var mappingException = assertThrows(MappingException.class, () -> eventDelegator.checkCorrectEvent(emptyGemsPubType));
        assertEquals("No Pubset Types Found in the Gems Event" ,mappingException.getMessage(), "The mapping exception does not match");
    }

    @Test
    void testWithBlankPubSetData(){
        var gemsPubTypeWithBlankPubSetData = new GEMSPubType();
        gemsPubTypeWithBlankPubSetData.setPubSet(Collections.emptyList());

        var mappingException = assertThrows(MappingException.class, () -> eventDelegator.checkCorrectEvent(gemsPubTypeWithBlankPubSetData));
        assertEquals("No Pubset Types Found in the Gems Event" ,mappingException.getMessage(), "The mapping exception does not match");
    }


    @Test
    void testWithBlankPubSet(){
        var gemsPubTypeWithBlankPubSetData = new GEMSPubType();
        gemsPubTypeWithBlankPubSetData.setPubSet(List.of(new PubSetType()));
        when(eventMapper.fromPubSetTypeToEvent(any())).thenReturn(null);

        var mappingException = assertThrows(MappingException.class, () -> eventDelegator.checkCorrectEvent(gemsPubTypeWithBlankPubSetData));
        assertEquals("The pubset data does not contain any Event Act" ,mappingException.getMessage(), "The mapping exception does not match");
    }


    @Test
    void testWithPubSetContainingShipmentEvent(){
        var gemsPubTypeWithBlankPubSetData = getGemsData(List.of(getPubSetTypeWithConfirm_Shipment_ClosedEventAct()));
        when(eventMapper.fromPubSetTypeToEvent(getPubSetTypeWithConfirm_Shipment_ClosedEventAct())).thenReturn(getEventForShipmentEventType());
        when(eventTypeMapper.asDCSAEventType(any())).thenReturn(EventType.SHIPMENT);
        when(shipmentMapper.fromPubSetTypeToShipmentEvent(getPubSetTypeWithConfirm_Shipment_ClosedEventAct(), getEventForShipmentEventType())).thenReturn(new ShipmentEvent());
        when(metricsService.incrementRecievedEventType(any())).thenReturn(Mono.empty());
        eventDelegator.checkCorrectEvent(gemsPubTypeWithBlankPubSetData);
        verify(shipmentMapper, times(1)).fromPubSetTypeToShipmentEvent(getPubSetTypeWithConfirm_Shipment_ClosedEventAct(), getEventForShipmentEventType());
        verifyNoInteractions(equipmentMapper);
        verifyNoInteractions(transportMapper);
    }

    @Test
    void testWithPubSetContainingEquipmentEvent(){
        var gemsPubTypeWithEquipmentPubSetData = getGemsData(List.of(getPubSetTypeWithARRIVECUIMPNEventAct()));
        when(eventMapper.fromPubSetTypeToEvent(getPubSetTypeWithARRIVECUIMPNEventAct())).thenReturn(getEventForEquipmentEventType());
        when(equipmentMapper.fromPubSetToEquipmentEvent(getPubSetTypeWithARRIVECUIMPNEventAct(), getEventForEquipmentEventType())).thenReturn(new EquipmentEvent());
        when(eventTypeMapper.asDCSAEventType(any())).thenReturn(EventType.EQUIPMENT);
        when(metricsService.incrementRecievedEventType(any())).thenReturn(Mono.empty());


        eventDelegator.checkCorrectEvent(gemsPubTypeWithEquipmentPubSetData);
        verify(equipmentMapper, times(1)).fromPubSetToEquipmentEvent(getPubSetTypeWithARRIVECUIMPNEventAct(), getEventForEquipmentEventType());
        verifyNoInteractions(shipmentMapper);
        verifyNoInteractions(transportMapper);
    }


    @Test
    void testWithPubSetContainingTransportEvent(){
        var gemsPubTypeWithTransportPubSetData = getGemsData(List.of(getPubSetTypeWithCONTAINER_ARRIVALEventAct()));
        when(eventMapper.fromPubSetTypeToEvent(getPubSetTypeWithCONTAINER_ARRIVALEventAct())).thenReturn(getEventForTransportEventType());
        when(transportMapper.fromPubSetToTransportEvent(getPubSetTypeWithCONTAINER_ARRIVALEventAct(), getEventForTransportEventType())).thenReturn(new TransportEvent());
        when(eventTypeMapper.asDCSAEventType(any())).thenReturn(EventType.TRANSPORT);
        when(metricsService.incrementRecievedEventType(any())).thenReturn(Mono.empty());


        eventDelegator.checkCorrectEvent(gemsPubTypeWithTransportPubSetData);
        verify(transportMapper, times(1)).fromPubSetToTransportEvent(getPubSetTypeWithCONTAINER_ARRIVALEventAct(), getEventForTransportEventType());
        verifyNoInteractions(shipmentMapper);
        verifyNoInteractions(equipmentMapper);
    }
}
