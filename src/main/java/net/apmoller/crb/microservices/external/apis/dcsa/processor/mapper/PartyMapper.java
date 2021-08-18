package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.Party;
import MSK.com.gems.PartyType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.ShipmentType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static MSK.com.external.dcsa.PartyFuncName.ANP;
import static MSK.com.external.dcsa.PartyFunctionCode.N2;
import static MSK.com.external.dcsa.PartyFunctionCode.NI;
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
                        partiesList.add(counter, getPartiesFromEvent(s.get(counter))))
        );

        /*special case for NI*/
        var counter = 0;
        for (Party party : partiesList) {
            if (Objects.nonNull(party.getPartyFunctionCode()) && party.getPartyFunctionCode().equals(N2)) {
                counter++;
            }
            if (counter > 1) {
                party.setPartyFunctionCode(NI);
                party.setPartyFunctionName(ANP);
            }
        }

        return partiesList;

    }

    protected static Party getPartiesFromEvent(PartyType partyType ) {

        var roleType = getNullSafeStringFromNullableChars(partyType.getRoletyp());
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
