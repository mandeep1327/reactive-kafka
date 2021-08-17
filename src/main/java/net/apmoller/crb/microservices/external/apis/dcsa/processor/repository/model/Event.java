package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import MSK.com.external.dcsa.CarrierCode;
import MSK.com.external.dcsa.EventClassifierCode;
import MSK.com.external.dcsa.EventType;
import MSK.com.external.dcsa.Party;
import MSK.com.external.dcsa.References;
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
    private CarrierCode carrierCode;
}
