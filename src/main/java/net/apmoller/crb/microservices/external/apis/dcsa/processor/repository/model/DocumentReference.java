package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DocumentReference {

    public enum Key {
        BKG, TRD
    }

    private Key key;
    private String value;
}

