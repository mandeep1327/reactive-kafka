package net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper;

import MSK.com.external.dcsa.DocumentReference;
import MSK.com.external.dcsa.DocumentReferenceType;
import MSK.com.gems.PubSetType;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getBolNumber;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.utils.EventUtility.getBookingNumber;

@UtilityClass
public final class DocumentReferenceMapper {

    public static List<DocumentReference> fromPubsetTypeToDocumentReferences(PubSetType pubSetType) {

        var docRef1 = DocumentReference.newBuilder().setDocumentReferenceType(DocumentReferenceType.BKG).setDocumentReferenceValue(getBookingNumber(pubSetType)).build();
        var docRef2 = DocumentReference.newBuilder().setDocumentReferenceType(DocumentReferenceType.TRD).setDocumentReferenceValue(getBolNumber(pubSetType)).build();
        return Stream.of(docRef1, docRef2)
                .filter(docRefs -> !Objects.isNull(docRefs.getDocumentReferenceValue()))
                .collect(Collectors.toUnmodifiableList());
    }

}
