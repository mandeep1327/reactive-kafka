package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maersk.jaxb.pojo.PubSetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EquipmentEventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.ElasticDataDump;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.springframework.stereotype.Service;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.EQUIPMENT_EVENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentEventDelegator {

    private final EquipmentEventMapper mapper;
    private final ObjectMapper objectMapper;

    public void createAndPushEquipmentEvent(Event baseEvent, PubSetType pubSetType) {
            var equipmentEvent = mapper.fromPubSetToEquipmentEvent(pubSetType, baseEvent);
            String rawEventData = null;
            try {
                rawEventData = objectMapper.writeValueAsString(equipmentEvent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            var dataToBePersisted = ElasticDataDump.builder().
                    typeOfEvent(EQUIPMENT_EVENT).uniqueEventId(baseEvent.getEventID()).eventDocument(rawEventData).build();
            log.info(rawEventData);
    }
}
