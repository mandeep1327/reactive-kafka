package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EquipmentEvent extends Event {

    private enum EquipmentEventType {
        LOAD, DISC, GTIN, GTOT, STUF, STRP
    }

    private enum EmptyIndicatorCode {
        EMPTY, LADEN
    }

    private EquipmentEventType equipmentEventType;
    private EmptyIndicatorCode emptyIndicatorCode;
    private List<DocumentReference> documentReferences;
    private Equipment equipment;
    private TransportCall transportCall;
}
