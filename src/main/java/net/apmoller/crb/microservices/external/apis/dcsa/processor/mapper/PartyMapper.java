package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.Party;
import com.maersk.jaxb.pojo.PartyType;
import com.maersk.jaxb.pojo.PubSetType;
import com.maersk.jaxb.pojo.ShipmentType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getMapOfPartyRoleAndFunctions;

@Component
public final class PartyMapper {

    protected PartyMapper(){}

    public static List<Party> getPartiesFromPubSetType(PubSetType pubSetType) {
        var parties = Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getParty);

        List<Party> partiesList = new ArrayList<>();

        parties.ifPresent(s -> IntStream.range(0, s.size())
                .forEach(counter ->
                        partiesList.add(counter, getPartiesFromEvent(s.get(counter), counter)))
        );

        return partiesList;

    }

    protected static Party getPartiesFromEvent(PartyType partyType, Integer counter ) {

        var roleType = getNullSafeStringFromNullableChars(partyType.getRoletyp());
        //TODO: take care of the extra role functions here with counter
        var partyFunctions = getMapOfPartyRoleAndFunctions().get(Integer.valueOf(roleType));
        return Party.newBuilder()
                .setPartyID(getNullSafeStringFromNullableChars(partyType.getCustNo()))
                .setPartyName(getNullSafeStringFromNullableChars(partyType.getCustName()))
                .setPartyFunctionCode(partyFunctions.getFunctionCode())
                .setPartyFunctionName(partyFunctions.getFunctionName())
                .build();

    }

    protected static String getNullSafeStringFromNullableChars(CharSequence chars) {
        return Optional.ofNullable(chars).map(CharSequence::toString).orElse(null);
    }

}
