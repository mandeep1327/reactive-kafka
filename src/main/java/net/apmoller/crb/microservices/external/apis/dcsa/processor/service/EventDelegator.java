package net.apmoller.crb.microservices.external.apis.dcsa.processor.service;

import com.maersk.jaxb.pojo.GEMSPubType;
import com.maersk.jaxb.pojo.PubSetType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.EventMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDelegator {

    private final EventMapper eventMapper;
    private final ShipmentEventDelegator shipmentEventDelegator;
    private final EquipmentEventDelegator equipmentEventDelegator;
    private final TransportEventDelegator transportEventDelegator;
    private static final String PUBSET_IS_EMPTY = "The Pubset element is empty";

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
        var baseEvent = getBaseEventFromGemSEvent(gemsBaseStructure);
        switch (baseEvent.getEventType()){
            case SHIPMENT:
                shipmentEventDelegator.createAndPushShipmentEvent(baseEvent, pubSetType);
/*            case TRANSPORT:
                transportEventDelegator.createAndPushTransportEvent(baseEvent, pubSetType);
            case EQUIPMENT:
                equipmentEventDelegator.createAndPushEquipmentEvent(baseEvent, pubSetType);*/
        }
    }
}


