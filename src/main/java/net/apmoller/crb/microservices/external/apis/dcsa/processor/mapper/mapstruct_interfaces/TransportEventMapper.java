package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces;

import MSK.com.external.dcsa.TransportEvent;
import com.maersk.jaxb.pojo.PubSetType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.DocumentReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.ReferenceMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.TransportCallMapper;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = {TransportCallMapper.class, EventUtility.class, ReferenceMapper.class, DocumentReferenceMapper.class})
public interface TransportEventMapper {

    @Mapping(expression = "java(EventUtility.getTPEventTypeFromPubSetType(pubSetType))", target = "transportEventType")
    @Mapping(expression = "java(TransportCallMapper.fromPubsetToTransportCall(pubSetType))", target = "transportCall")
    @Mapping(expression = "java(DocumentReferenceMapper.fromPubsetTypeToDocumentReferences(pubSetType))", target = "documentReferences")
    TransportEvent fromPubSetToTransportEvent(PubSetType pubSetType, Event baseEvent);


}
