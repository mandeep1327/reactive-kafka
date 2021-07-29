package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * This class uses SuperBuilder as the other Event Objects. The reason is to make the super class
 * elements accessible. Also Mapstruct works pretty well with the SuperBuilder. Note without the
 * @Builder annotation Mapstruct can also work , but needs the @Data in that case. @Builder gives
 * convenience to developer for creating the objects
 * Author - A. Das
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
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
