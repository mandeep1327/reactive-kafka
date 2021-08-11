package net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemoClass {
    private ShipmentEvent shipmentEvent;
    private EquipmentEvent equipmentEvent;
    private TransportEvent transportEvent;
}
