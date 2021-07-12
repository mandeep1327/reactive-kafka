package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class References {

    public enum RefTypeEnum {
        FF, SI, PO, AAO,CR
    }
    private RefTypeEnum referenceType;
    private String referenceValue;
}
