package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maersk.jaxb.pojo.PubSetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ElasticDataDump;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.springframework.stereotype.Service;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.SHIPMENT_EVENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentEventDelegator {

    private final ShipmentEventMapper mapper;
    private final ObjectMapper objectMapper;

    public void createAndPushShipmentEvent(Event baseEvent, PubSetType pubSetType) {
        var shipmentEvent = mapper.fromPubSetTypeToShipmentEvent(pubSetType, baseEvent);
        String rawEventData = null;
        try {
            rawEventData = objectMapper.writeValueAsString(shipmentEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        var dataToBePersisted = ElasticDataDump.builder().
                typeOfEvent(SHIPMENT_EVENT).uniqueEventId(baseEvent.getEventID()).eventDocument(rawEventData).build();
        log.info(rawEventData);
    }

}
