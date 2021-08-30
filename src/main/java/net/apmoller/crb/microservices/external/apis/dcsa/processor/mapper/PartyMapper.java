package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.Party;
import MSK.com.gems.PartyType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.ShipmentType;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static MSK.com.external.dcsa.PartyFunctionCode.N2;
import static MSK.com.external.dcsa.PartyFunctionCode.NI;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getMapOfPartyRoleAndFunctions;

@UtilityClass
public final class PartyMapper {

    public static List<Party> getPartiesFromPubSetType(PubSetType pubSetType) {
        var partiesList = Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getParty)
                .map(s -> s.stream().map(PartyMapper::getPartiesFromEvent)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()))
                .orElse(List.of());

        partiesList.stream()
                .filter(party -> N2.equals(party.getPartyFunctionCode()))
                .skip(1)
                .forEach(PartyMapper::setAsAdditionalNotifyParty);

        return partiesList;
    }


    private static Party getPartiesFromEvent(PartyType partyType) {

        var roleType = Integer.valueOf(partyType.getRoletyp());
        if (getMapOfPartyRoleAndFunctions().containsKey(roleType)) {
            var partyFunctions = getMapOfPartyRoleAndFunctions().get(roleType);
            return Party.newBuilder()
                    .setPartyID(partyType.getCustNo())
                    .setPartyName(partyType.getCustName())
                    .setPartyFunctionCode(partyFunctions.getFunctionCode())
                    .setPartyFunctionName(partyFunctions.getFunctionName())
                    .build();
        }
        return null;
    }

    private static void setAsAdditionalNotifyParty(Party party) {
        party.setPartyFunctionCode(NI);
        party.setPartyFunctionName("Additional Notify Party");
    }

}
