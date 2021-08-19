package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.gems.PubSetType;
import MSK.com.gems.ShipmentType;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public final class ServiceTypeMapper {

    public static String getServiceTypeFromPubSetType(PubSetType pubSetType) {

        return Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getRcvSvc)
                .map(CharSequence::toString)
                .filter(s -> !s.isEmpty())
                .map(fp -> fp.concat("/")
                        .concat(Optional.ofNullable(pubSetType.getShipment())
                                .map(ShipmentType::getDelSvc)
                                .map(CharSequence::toString).orElse("")
                        ))
                .orElse(null);
    }

}
