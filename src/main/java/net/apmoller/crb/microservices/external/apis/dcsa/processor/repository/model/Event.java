package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Container(containerName = "Events", ru = "400")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public abstract class Event {

    @Id
    private String eventID;
    @PartitionKey
    private String bookingReference;
    private String eventDateTime;
    private String eventType;
    private String eventCreatedDateTime;
    private String eventClassifierCode;

}
