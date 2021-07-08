package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Container(containerName = "Events", ru = "400")
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class Event {

    public enum  EventType {
        EQUIPMENT, SHIPMENT, TRANSPORT;
    }

    private enum  EventClassifierCode {
        PLN, ACT, EST;
    }

    @Id
    private String eventID;
    @PartitionKey
    private String bookingReference;
    private String eventDateTime;
    private EventType eventType;
    private String eventCreatedDateTime;
    private EventClassifierCode eventClassifierCode;

}
