package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EquipmentEvent extends Event {

    private String equipmentEventType;
    private String emptyIndicatorCode;
    private List<Map<String, String>> documentReferences;
    private Equipment equipment;
    private TransportCall transportCall;
}
