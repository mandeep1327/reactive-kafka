package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import com.maersk.jaxb.pojo.EventType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "startLocation.maerskCityGeoId", source = "fromLocation.cityGeoId")
    @Mapping(target = "startLocation.maerskSiteGeoId", source = "fromLocation.siteGeoId")
    @Mapping(target = "endLocation.maerskCityGeoId", source = "toLocation.cityGeoId")


    @Mapping(target = "eventType", source = "java(getDCSAEventType(eventAct))")
    @Mapping(target = "eventCreatedDateTime", source = "transport.vessel.code")
    @Mapping(target = "eventClassifierCode", source = "serviceCode")
    Event fromEventTypeToEvent(EventType details);

    default Event.EventType getDCSAEventType(String act) {
        

    }

}
