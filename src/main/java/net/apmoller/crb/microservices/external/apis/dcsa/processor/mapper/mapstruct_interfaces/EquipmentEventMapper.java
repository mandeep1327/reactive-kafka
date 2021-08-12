package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import com.maersk.jaxb.pojo.MoveType;
import com.maersk.jaxb.pojo.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DocumentReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.PartyMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ServiceTypeMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Seals;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.TransportCall;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.TransportCallMapper.fromPubsetToTransportCall;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.TransportCallMapper.fromPubsetToTransportCallBase;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EmptyIndicatorCode.EMPTY;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.EquipmentEvent.EmptyIndicatorCode.LADEN;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Seals.SealSourceEnum.CAR;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Seals.SealSourceEnum.CUS;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Seals.SealSourceEnum.SHI;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Seals.SealSourceEnum.VET;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getEquipmentEventType;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getFirstEquipmentElement;

@Mapper(componentModel = "spring",
        imports = {EventUtility.class, PartyMapper.class, ReferenceMapper.class, ServiceTypeMapper.class, DocumentReferenceMapper.class})
public interface EquipmentEventMapper {

    @Mapping(expression = "java(getEquipmentEventTypeFromEventAct(pubSetType.getEvent().getEventAct().toString()))", target = "equipmentEventType")
    @Mapping(expression = "java(fromPubSetTypeToEmptyIndicatorCode(pubSetType))", target = "emptyIndicatorCode")
    @Mapping(expression = "java(DocumentReferenceMapper.fromPubsetTypeToDocumentReferences(pubSetType))", target = "documentReferences")
    @Mapping(expression = "java(fromPubSetTypeToTransportCall(pubSetType))", target = "transportCall")
    @Mapping(expression = "java(fromPubSetTypeToIsoEquipmentCode(pubSetType))", target = "isoEquipmentCode")
    @Mapping(expression = "java(fromPubSetTypeToSeals(pubSetType))", target = "seals")

    EquipmentEvent fromPubSetToEquipmentEvent(PubSetType pubSetType, Event baseEvent);


    default EquipmentEvent.EquipmentEventType getEquipmentEventTypeFromEventAct(String eventAct) {
        return getEquipmentEventType(eventAct);
    }

    default EquipmentEvent.EmptyIndicatorCode fromPubSetTypeToEmptyIndicatorCode(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getMove())
                .map(MoveType::getStatEmpty)
                .map(CharSequence::toString)
                .filter(stat -> stat.equals("Y"))
                .map(s -> EMPTY)
                .orElse(LADEN);
    }

    default TransportCall fromPubSetTypeToTransportCall(PubSetType pubSetType) {
        var transportCall = fromPubsetToTransportCallBase(pubSetType);
        return TransportCall.builder()
                .carrierServiceCode(transportCall.getCarrierServiceCode())
                .facilityTypeCode(transportCall.getFacilityTypeCode())
                .otherFacility(transportCall.getOtherFacility())
                .build();
    }

    default String fromPubSetTypeToIsoEquipmentCode(PubSetType pubSetType) {
        return Optional.ofNullable(getFirstEquipmentElement(pubSetType).getIsocode())
                .map(CharSequence::toString)
                .orElse(null);
    }

    default List<Seals> fromPubSetTypeToSeals(PubSetType pubSetType) {
        var sealList = Optional.ofNullable(getFirstEquipmentElement(pubSetType).getMove())
                .map(MoveType::getSeal)
                .filter(sealTypes -> !sealTypes.isEmpty())
                .orElse(Collections.emptyList());

        List<Seals> seals = new ArrayList<>();

        sealList.forEach(sealType ->
                seals.add(Seals.builder()
                        .sealNumber(sealType.getValue().toString())
                        .sealSource(getSealSource(sealType.getTyp().toString()))
                        .build()));

        return seals;
    }

    private Seals.SealSourceEnum getSealSource(String sealType) {
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
