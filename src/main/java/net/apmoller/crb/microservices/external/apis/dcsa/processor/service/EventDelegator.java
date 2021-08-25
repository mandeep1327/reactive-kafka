package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import MSK.com.gems.GEMSPubType;
import MSK.com.gems.PubSetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.MappingException;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DCSAEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.TransportEventMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEventAct;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDelegator {

    private final EventMapper eventMapper;
    private final ShipmentEventMapper shipmentEventMapper;
    private final EquipmentEventMapper equipmentEventMapper;
    private final TransportEventMapper transportEventMapper;
    private final DCSAEventTypeMapper eventTypeMapper;
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

    public void checkCorrectEvent(GEMSPubType gemsBaseStructure) {
        var pubSetType = getPubSetType(gemsBaseStructure).orElseThrow(getExceptionSupplier(PUBSET_IS_EMPTY));
        var eventAct = getEventAct(pubSetType);
        if (eventTypeMapper.asDCSAEventType(eventAct) != null) {
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
            log.info("The payload is this : {}", dcsaTrackAndTraceToBeStored);
            sendMessage(dcsaTrackAndTraceToBeStored, keyForKafkaPayload);
        } else {
            log.warn("The payload of event type {} was dropped" , eventAct);
        }
    }

    private String getKeyForKafkaPayload(String eventId) {
        return Optional.ofNullable(eventId)
                .orElse(UUID.randomUUID().toString());
    }

    private void sendMessage(DcsaTrackTraceEvent dcsaTrackAndTraceToBeStored, String keyForKafkaPayload) {
        log.info("The payload is this : {}", dcsaTrackAndTraceToBeStored);

        var senderRecord = Mono.just(SenderRecord.create(
                new ProducerRecord<>(kafkaPublisherTopic, keyForKafkaPayload, dcsaTrackAndTraceToBeStored), keyForKafkaPayload));

        kafkaSender.send(senderRecord)
                .doOnError(e -> log.error("This is the error message" + e.getMessage()))
                .subscribe(r -> {
                    var metadata = r.recordMetadata();
                    log.info("Message {} sent successfully, topic-partition={}-{} offset={}\n",
                            r.correlationMetadata(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset());
                });
    }
}


