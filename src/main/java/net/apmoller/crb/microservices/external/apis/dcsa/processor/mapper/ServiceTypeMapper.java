package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import com.maersk.jaxb.pojo.PubSetType;
import com.maersk.jaxb.pojo.ShipmentType;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class ServiceTypeMapper {

    protected ServiceTypeMapper(){}

    public static String getServiceTypeFromPubSetType(PubSetType pubSetType) {

        return Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getRcvSvc)
                .map(CharSequence::toString)
                .filter(s -> !s.isEmpty())
                .map(fp -> fp.concat("/")
                        .concat(
                                Optional.ofNullable(pubSetType.getShipment())
                                        .map(ShipmentType::getDelSvc)
                                        .map(CharSequence::toString).orElse("")
                        ))
                .orElse(null);
    }

}
