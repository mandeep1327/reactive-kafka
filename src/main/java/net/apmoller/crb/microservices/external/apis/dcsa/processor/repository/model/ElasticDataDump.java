package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElasticDataDump {
    private String uniqueEventId;
    private String typeOfEvent;
    private String eventDocument;
}
