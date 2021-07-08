package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import java.util.List;
import java.util.Map;

public class TransportEvent extends Event {

    private String transportEventType;
    private String delayReasonCode;
    private String changeRemark;
    private List<Map<String, String>> documentReferences;
    private TransportCall transportCall;
}
