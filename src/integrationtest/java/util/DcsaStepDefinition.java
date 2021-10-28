package util;

import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import MSK.com.external.dcsa.EventClassifierCode;
import MSK.com.external.dcsa.TransPortMode;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DcsaStepDefinition extends CucumberSpringConfiguration {

    List<ConsumerRecord<String, DcsaTrackTraceEvent>> changeEvents;
    SpecificRecordBase event;

    @Given("set a message on kafka with {string}")
    public void send(String jsonfile) {
        var jsonContent = TestDataUtils.loadDataJson(jsonfile);
        KafkaTestContainer.sendToProducer(jsonContent);
    }

    @Then("the EMPv2 topic should produce a message")
    public void theEMPvTopicShouldProduceAMessage() {
        changeEvents = KafkaTestContainer.drain(1);
        System.out.println("EVENTS " + changeEvents.get(0));
    }

    @And("it should be of type {string}")
    public void itShouldBeOfType(String type) {
        DcsaTrackTraceEvent event = changeEvents.get(0).value();
        if ("SHIPMENT".equals(type)) {
            assertTrue(!isNull(event.getShipmentEvent()) &&
                    isNull(event.getTransportEvent()) &&
                    isNull(event.getEquipmentEvent()));
            this.event = event.getShipmentEvent();
        } else if ("TRANSPORT".equals(type)) {
            assertTrue(isNull(event.getShipmentEvent()) &&
                            !isNull(event.getTransportEvent()) &&
                            isNull(event.getEquipmentEvent()));
            this.event = event.getTransportEvent();
        } else if ("EQUIPMENT".equals(type)) {
            assertTrue(isNull(event.getShipmentEvent()) &&
                    isNull(event.getTransportEvent()) &&
                    !isNull(event.getEquipmentEvent()));
            this.event = event.getEquipmentEvent();
        } else {
            fail("Unsupported event type");
        }
    }

    @And("the event id should be {string}")
    public void theEventIdShouldBe(String eventId) {
        assertEquals(this.event.get("eventID"), eventId);
    }

    @And("the mode of transport should be {string}")
    public void theModeOfTransportShouldBe(String modeOfTransport) {
        if(!modeOfTransport.isBlank()) {
            TransPortMode transportMode =(TransPortMode) ((SpecificRecordBase) this.event.get("transportCall")).get("modeOfTransport");
            assertEquals(modeOfTransport, transportMode.toString());
        }
    }

    @And("the eventClassifierCode should be {string}")
    public void theEventClassifierCodeShouldBe(String eventClassifierCode) {
        assertEquals(this.event.get("eventClassifierCode"), EventClassifierCode.valueOf(eventClassifierCode));
    }
}
