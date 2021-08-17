package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import com.maersk.jaxb.pojo.GEMSPubType;
import com.maersk.jaxb.pojo.PubSetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDelegator {

    private final EventMapper eventMapper;
    private final ShipmentEventMapper shipmentEventMapper;
    private final EquipmentEventMapper equipmentEventMapper;
    private final TransportEventMapper transportEventMapper;
    private static final String PUBSET_IS_EMPTY = "The Pubset element is empty";
    private final KafkaSender<String, DcsaTrackTraceEvent> kafkaSender;

    @Value("${kafka.publisher.topic}")
    private String kafkaPublisherTopic;

    private Event getBaseEventFromGemSEvent(GEMSPubType gemsBaseStructure) {
        return getPubSetType(gemsBaseStructure)
                .map(eventMapper::fromPubSetTypeToEvent)
                .orElseThrow(getExceptionSupplier(PUBSET_IS_EMPTY));
    }

    @NotNull
    private Supplier<MappingException> getExceptionSupplier(String pubsetIsEmpty) {
        return () -> new MappingException(pubsetIsEmpty);
    }

    @NotNull
    private Optional<PubSetType> getPubSetType(GEMSPubType gemsBaseStructure) {
        return Optional.ofNullable(gemsBaseStructure.getPubSet())
                .filter(pubSetTypes -> !pubSetTypes.isEmpty())
                .orElseThrow(getExceptionSupplier("No Pubset Types Found in the Gems Event"))
                .stream()
                .findFirst();
    }

    public Flux<SenderResult<String>> checkCorrectEvent(GEMSPubType gemsBaseStructure) {
        var pubSetType = getPubSetType(gemsBaseStructure).orElseThrow(getExceptionSupplier(PUBSET_IS_EMPTY));
        var baseEvent = getBaseEventFromGemSEvent(gemsBaseStructure);
        var dcsaTrackAndTraceToBeStored = new DcsaTrackTraceEvent();
        String keyForKafkaPayload;
        switch (baseEvent.getEventType()) {
            case SHIPMENT:
                var shipmentEvent = shipmentEventMapper.fromPubSetTypeToShipmentEvent(pubSetType, baseEvent);
                dcsaTrackAndTraceToBeStored.setShipmentEvent(shipmentEvent);
                keyForKafkaPayload = getKeyForKafkaPayload(shipmentEvent.getEventID());
                break;
            case TRANSPORT:
                var transportEvent = transportEventMapper.fromPubSetToTransportEvent(pubSetType, baseEvent);
                dcsaTrackAndTraceToBeStored.setTransportEvent(transportEvent);
                keyForKafkaPayload = getKeyForKafkaPayload(transportEvent.getEventID());
                break;
            case EQUIPMENT:
                var equipmentEvent = equipmentEventMapper.fromPubSetToEquipmentEvent(pubSetType, baseEvent);
                dcsaTrackAndTraceToBeStored.setEquipmentEvent(equipmentEvent);
                keyForKafkaPayload = getKeyForKafkaPayload(equipmentEvent.getEventID());
                break;
            default:
                throw new MappingException("Not acceptable event type of type" + dcsaTrackAndTraceToBeStored);
        }

        return sendMessage(dcsaTrackAndTraceToBeStored, keyForKafkaPayload);
    }

    private String getKeyForKafkaPayload(CharSequence eventId) {
        return (String) Optional.ofNullable(eventId).orElse(UUID.randomUUID().toString());
    }

    private Flux<SenderResult<String>> sendMessage(DcsaTrackTraceEvent dcsaTrackAndTraceToBeStored, String keyForKafkaPayload) {
        var senderRecord = Mono.just(SenderRecord.create(new ProducerRecord<>(kafkaPublisherTopic, keyForKafkaPayload, dcsaTrackAndTraceToBeStored), keyForKafkaPayload));

        return kafkaSender.send(senderRecord)
                .doOnError(e -> log.error("This is the error message" +e.getMessage()));
    }
}


