package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.DocumentReference;
import MSK.com.external.dcsa.Key;
import MSK.com.gems.PubSetType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getBolNumber;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getBookingNumber;

@Component
public final class DocumentReferenceMapper {

    protected DocumentReferenceMapper(){}

    public static List<DocumentReference> fromPubsetTypeToDocumentReferences(PubSetType pubSetType) {

        var docRef1 = DocumentReference.newBuilder().setKey(Key.BKG).setValue(getBookingNumber(pubSetType)).build();
        var docRef2 = DocumentReference.newBuilder().setKey(Key.TRD).setValue(getBolNumber(pubSetType)).build();
        return Stream.of(docRef1, docRef2)
                .filter(docRefs -> !Objects.isNull(docRefs.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

}
