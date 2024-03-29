package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.RefTypeEnum;
import MSK.com.gems.PartyType;
import MSK.com.gems.PubSetType;
import MSK.com.gems.ShipmentType;
import MSK.com.external.dcsa.References;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static MSK.com.external.dcsa.RefTypeEnum.AAO;
import static MSK.com.external.dcsa.RefTypeEnum.FF;
import static MSK.com.external.dcsa.RefTypeEnum.PO;
import static MSK.com.external.dcsa.RefTypeEnum.SI;

@UtilityClass
public final class ReferenceMapper {

    public static List<References> getReferencesFromPubSetType(PubSetType pubSetType) {
        var chosenParties = Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getParty)
                .filter(partyTypes -> !partyTypes.isEmpty())
                .orElse(Collections.emptyList())
                .stream()
                .filter(partyType ->
                        "15".equals((partyType.getRoletyp())) ||
                        "3".equals((partyType.getRoletyp())) ||
                        "4".equals((partyType.getRoletyp())))
                .collect(Collectors.toUnmodifiableList());

        var references = new ArrayList<>(getReferenceFromShipmentOfRefType41(pubSetType));

        if (!chosenParties.isEmpty()) {
            chosenParties.forEach(partyType -> addReferencesForChosenParties(references, partyType));
        }
        return references;
    }

    protected static List<References> getReferenceFromShipmentOfRefType41(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getReference)
                .filter(referenceTypes -> !referenceTypes.isEmpty())
                .orElse(Collections.emptyList())
                .stream()
                .filter(referenceType -> "41".equals((referenceType.getTyp())))
                .filter(referenceType -> !Objects.isNull(referenceType.getValue()))
                .map(s -> References.newBuilder()
                        .setReferenceType(PO)
                        .setReferenceValue(s.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    protected static void addReferencesForChosenParties(List<References> referenceList, PartyType party) {
        var streamOfCustomerReference = getStreamOfCustomerReference(party);
        switch (party.getRoletyp()) {
            case "15":
                buildReferenceList(referenceList, streamOfCustomerReference, FF);
                break;
            case "3":
                buildReferenceList(referenceList, streamOfCustomerReference, SI);
                break;
            case "4":
                buildReferenceList(referenceList, streamOfCustomerReference, AAO);
                break;
            default:
                break;
        }
    }

    protected static void buildReferenceList(List<References> referenceList, Stream<String> streamOfCustomerReference, RefTypeEnum refTypeEnum) {
        streamOfCustomerReference.forEach(buildReference(referenceList, refTypeEnum));
    }

    protected static Stream<String> getStreamOfCustomerReference(PartyType party) {
        return Optional.ofNullable(party.getCustRefNo())
                .filter(custRefs -> !custRefs.isEmpty())
                .orElse(Collections.emptyList())
                .stream();
    }

    private static Consumer<String> buildReference(List<References> referenceList, RefTypeEnum refTypeEnum) {
        return customerReference -> referenceList.add(References.newBuilder()
                .setReferenceType(refTypeEnum)
                .setReferenceValue(customerReference)
                .build()
        );
    }
}
