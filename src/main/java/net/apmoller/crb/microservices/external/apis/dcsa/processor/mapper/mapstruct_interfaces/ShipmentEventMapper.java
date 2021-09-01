package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.external.dcsa.ShipmentEvent;
import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DocumentIdMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ShipmentEventTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ShipmentInformationTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class},
        uses = {ShipmentEventTypeMapper.class, ShipmentInformationTypeMapper.class, DocumentIdMapper.class})
public interface ShipmentEventMapper {
    @Mapping(source = "pubSetType.event.eventAct", target = "shipmentEventType")
    @Mapping(source = "pubSetType.event.eventAct", target = "shipmentInformationType")
    @Mapping(source = "pubSetType", target = "documentID")
    ShipmentEvent fromPubSetTypeToShipmentEvent(PubSetType pubSetType, Event baseData);

}
