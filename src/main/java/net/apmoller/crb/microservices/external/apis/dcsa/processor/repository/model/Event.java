package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
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

    private String eventID;
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
