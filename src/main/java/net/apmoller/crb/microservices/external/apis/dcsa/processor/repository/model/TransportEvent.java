package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

public class TransportEvent extends Event {

    private String transportEventType;
    private String delayReasonCode;
    private String changeRemark;
    private List<DocumentReference> documentReferences;
    private TransportCall transportCall;
}
