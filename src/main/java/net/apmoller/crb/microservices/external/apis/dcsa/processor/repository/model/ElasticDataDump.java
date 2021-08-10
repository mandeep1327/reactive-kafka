package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElasticDataDump {
    //primary Key
    private String uniqueEventId;
    //SHIPMENT,EQUIPMENT,TRANSPORT
    private String typeOfEvent;
    private String bookingNumber;
    private String eventDocument;
}
