package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.external.dcsa.EmptyIndicatorCode;
import MSK.com.external.dcsa.EquipmentEvent;
import MSK.com.external.dcsa.EquipmentEventType;
import MSK.com.external.dcsa.SealSource;
import MSK.com.external.dcsa.Seals;
import MSK.com.external.dcsa.TransportCall;
import MSK.com.gems.MoveType;
import MSK.com.gems.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DocumentReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ServiceTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.dto.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static MSK.com.external.dcsa.EmptyIndicatorCode.EMPTY;
import static MSK.com.external.dcsa.EmptyIndicatorCode.LADEN;
import static MSK.com.external.dcsa.SealSource.CAR;
import static MSK.com.external.dcsa.SealSource.CUS;
import static MSK.com.external.dcsa.SealSource.SHI;
import static MSK.com.external.dcsa.SealSource.VET;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.TransportCallMapper.fromPubsetToTransportCallBase;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEquipmentEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstEquipmentElement;

@Mapper(componentModel = "spring",
        imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class, ServiceTypeMapper.class, DocumentReferenceMapper.class})
public interface EquipmentEventMapper {

    @Mapping(expression = "java(getEquipmentEventTypeFromEventAct(pubSetType.getEvent().getEventAct()))", target = "equipmentEventType")
    @Mapping(expression = "java(fromPubSetTypeToEmptyIndicatorCode(pubSetType))", target = "emptyIndicatorCode")
    @Mapping(expression = "java(DocumentReferenceMapper.fromPubsetTypeToDocumentReferences(pubSetType))", target = "documentReferences")
    @Mapping(expression = "java(fromPubSetTypeToTransportCall(pubSetType))", target = "transportCall")
    @Mapping(expression = "java(fromPubSetTypeToIsoEquipmentCode(pubSetType))", target = "isoEquipmentCode")
    @Mapping(expression = "java(fromPubSetTypeToSeals(pubSetType))", target = "seals")
    EquipmentEvent fromPubSetToEquipmentEvent(PubSetType pubSetType, Event baseEvent);


    default EquipmentEventType getEquipmentEventTypeFromEventAct(String eventAct) {
        return getEquipmentEventType(eventAct);
    }

    default EmptyIndicatorCode fromPubSetTypeToEmptyIndicatorCode(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getMove())
                .map(MoveType::getStatEmpty)
                .filter(stat -> stat.equals("Y"))
                .map(s -> EMPTY)
                .orElse(LADEN);
    }

    default TransportCall fromPubSetTypeToTransportCall(PubSetType pubSetType) {
        return fromPubsetToTransportCallBase(pubSetType);
    }

    default String fromPubSetTypeToIsoEquipmentCode(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getIsocode())
                .orElse(null);
    }

    default List<Seals> fromPubSetTypeToSeals(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getMove())
                .map(MoveType::getSeal)
                .filter(sealTypes -> !sealTypes.isEmpty())
                .orElse(Collections.emptyList())
                .stream().map(sealType -> Seals.newBuilder()
                        .setSealNumber((sealType.getValue()))
                        .setSealSource(getSealSource((sealType.getTyp())))
                        .build())
                .collect(Collectors.toList());
    }

    private SealSource getSealSource(String sealType) {
        switch (sealType.toUpperCase()) {
            case "MAERSK":
                return CAR;
            case "SHIPPER":
                return SHI;
            case "VET":
                return VET;
            case "CUSTOMS":
                return CUS;
            default:
                return null;
        }
    }


}
