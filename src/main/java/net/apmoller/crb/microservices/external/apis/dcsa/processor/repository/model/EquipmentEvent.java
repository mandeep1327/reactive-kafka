package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EquipmentEvent extends Event {

    private String equipmentEventType;
    private String emptyIndicatorCode;
    private List<DocumentReference> documentReferences;
    private Equipment equipment;
    private TransportCall transportCall;
}
