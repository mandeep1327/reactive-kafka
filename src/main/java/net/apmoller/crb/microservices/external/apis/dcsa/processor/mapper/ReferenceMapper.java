package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import com.maersk.jaxb.pojo.PartyType;
import com.maersk.jaxb.pojo.PubSetType;
import com.maersk.jaxb.pojo.ShipmentType;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References.RefTypeEnum.AAO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References.RefTypeEnum.FF;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References.RefTypeEnum.PO;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.References.RefTypeEnum.SI;

@Component
public final class ReferenceMapper {

    protected ReferenceMapper(){}

    public static List<References> getReferencesFromPubSetType(PubSetType pubSetType) {
        var chosenParties= Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getParty)
                .filter(partyTypes -> !partyTypes.isEmpty())
                .orElse(Collections.emptyList())
                .stream()
                .filter(partyType -> partyType.getRoletyp()!=null
                        && ( partyType.getRoletyp().toString().equals("15")
                        || partyType.getRoletyp().toString().equals("3")
                        || partyType.getRoletyp().toString().equals("4")))
                .collect(Collectors.toUnmodifiableList());

        var specificReferenceType  = getReferenceFromShipmentOfRefType41(pubSetType);

        if (chosenParties.size() > 1) {
            return fetchReferencesFromChosenParties(chosenParties.get(0));
        } else {
            return specificReferenceType;
        }
    }

    protected static List<References> getReferenceFromShipmentOfRefType41(PubSetType pubSetType) {
        return Optional.ofNullable(pubSetType.getShipment())
                .map(ShipmentType::getReference)
                .filter(referenceTypes -> !referenceTypes.isEmpty())
                .orElse(Collections.emptyList())
                .stream()
                .filter(referenceType -> referenceType.getTyp().toString().equals("41"))
                .filter(referenceType -> !Objects.isNull(referenceType.getValue()))
                .findFirst()
                .map(s -> List.of(References.builder()
                        .referenceType(PO)
                        .referenceValue(s.getValue().toString())
                        .build()))
                .orElse(null);
    }

    protected static List<References> fetchReferencesFromChosenParties (PartyType party) {
        List<References> referenceList = new ArrayList<>();
        var streamOfCustomerReference = getStreamOfCustomerReference(party);
        switch (party.getRoletyp().toString()){
            case "15":
                buildReferenceList(referenceList, streamOfCustomerReference, FF);
                break;
            case "3":
                buildReferenceList(referenceList, streamOfCustomerReference, SI);
                break;
            case "4" :
                buildReferenceList(referenceList, streamOfCustomerReference, AAO);
                break;
            default:
                return Collections.emptyList();
        }
        return referenceList;
    }

    protected static void buildReferenceList(List<References> referenceList, Stream<String> streamOfCustomerReference, References.RefTypeEnum refTypeEnum) {
        streamOfCustomerReference.forEach(buildReference(referenceList, refTypeEnum));
    }

    protected static Stream<String> getStreamOfCustomerReference(PartyType party) {
        return Optional.ofNullable(party.getCustRefNo())
                .filter(custRefs -> !custRefs.isEmpty())
                .orElse(Collections.emptyList())
                .stream()
                .map(CharSequence::toString);
    }

    private static Consumer<String> buildReference(List<References> referenceList, References.RefTypeEnum refTypeEnum) {
        return customerReference -> referenceList.add(References.builder()
                .referenceType(refTypeEnum)
                .referenceValue(customerReference)
                .build()
        );
    }
}
