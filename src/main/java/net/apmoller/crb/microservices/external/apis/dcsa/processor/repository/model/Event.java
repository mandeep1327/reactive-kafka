package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    public enum  EventType {
        EQUIPMENT, SHIPMENT, TRANSPORT
    }

    public enum  EventClassifierCode {
        PLN, ACT, EST
    }

    public enum CarrierCodeEnum {
        MAEU, SAFM, MCCQ, SEJJ, SEAU
    }

    @Id
    private String eventID;
    @PartitionKey
    private String bookingReference;
    private String eventDateTime;
    private EventType eventType;
    private String eventCreatedDateTime;
    private EventClassifierCode eventClassifierCode;
    private List<Party> parties;
    private List<References> references;
    private String equipmentReference;
    private String carrierBookingReference;
    private String transportDocumentReference;
    private String sourceSystem;
    private String serviceType;
    private CarrierCodeEnum carrierCode;

}
