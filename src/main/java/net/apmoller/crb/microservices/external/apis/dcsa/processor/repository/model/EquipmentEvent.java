package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EquipmentEvent extends Event {

    public enum EquipmentEventType {
        LOAD, DISC, GTIN, GTOT, STUF, STRP
    }

    public enum EmptyIndicatorCode {
        EMPTY, LADEN
    }



    private EquipmentEventType equipmentEventType;
    private EmptyIndicatorCode emptyIndicatorCode;
    private List<DocumentReference> documentReferences;
    private String isoEquipmentCode;
    private List<Seals> seals;
    private TransportCall transportCall;
}
