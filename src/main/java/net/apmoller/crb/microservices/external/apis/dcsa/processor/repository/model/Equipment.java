package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Equipment {

    private enum WeightUnit {
        KGM, LBR;
    }

    private String equipmentReference;
    private String ISOEquipmentCode;
    private Integer tareWeight;
    private WeightUnit weightUnit;
    private Boolean isShipperOwned;


}
