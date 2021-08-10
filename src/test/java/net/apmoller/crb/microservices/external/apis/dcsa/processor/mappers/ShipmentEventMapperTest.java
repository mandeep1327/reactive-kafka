package net.apmoller.crb.microservices.external.apis.dcsa.processor.mappers;

import net.apmoller.crb.microservices.external.apis.dcsa.processor.mapper.mapstruct_interfaces.ShipmentEventMapperImpl;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.repository.model.Event;
import org.junit.jupiter.api.Test;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.GEMSPubTestDataBuilder.getGemsData;
import static net.apmoller.crb.microservices.external.apis.dcsa.processor.testDataBuilders.output.EventDataBuilder.getEventForShipmentEventType;

public class ShipmentEventMapperTest {

    private ShipmentEventMapperImpl shipmentEventMapper = new ShipmentEventMapperImpl();

    @Test
    void testShipmentEventData(){
        var gemsData = getGemsData();
        var pubsetData = gemsData.getPubSet().get(0);
        var baseEventData = getEventForShipmentEventType();

        var out = shipmentEventMapper.fromPubSetTypeToShipmentEvent(pubsetData, baseEventData);
        System.out.println(out);
    }
}
