package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class TransportEvent extends Event {

    public enum TransportEventType {
        ARRI, DEPA, OMIT
    }

    private TransportEventType transportEventType;
    private String delayReasonCode;
    private String changeRemark;
    private List<DocumentReference> documentReferences;
    private TransportCall transportCall;
    private String vesselCode;
}
