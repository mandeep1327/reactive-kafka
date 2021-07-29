package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maersk.jaxb.pojo.PubSetType;
import lombok.RequiredArgsConstructor;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ElasticDataDump;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
                typeOfEvent("SHIPMENT").uniqueEventId(baseEvent.getEventID()).eventDocument(rawEventData).build();
    }

}
