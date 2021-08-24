package util;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public class DcsaStepDefinition extends CucumberSpringConfiguration {
    private String jsonContent;

    @Given("set a message on kafka with {string}")
    public void send(String jsonfile) throws Exception {
        jsonContent = TestDataUtils.loadDataJson(jsonfile);
        KafkaTestContainer.sendToProducer(jsonContent);
    }

    @And("I wait external-dcsa-events-processor consumes the message")
    public void iWaitExternalDcsaEventsProcessorConsumesTheMessage() throws InterruptedException {
        Thread.sleep(5000);
    }

    @Then("the EMPv2 topic should produce a message")
    public void theEMPvTopicShouldProduceAMessage() throws Exception {
        KafkaTestContainer.setupKafkaConsumer();
        KafkaTestContainer.setupConfig( 1, 1);

        List<ConsumerRecord<String, String>> changeEvents =
                KafkaTestContainer.drain( 1);
        System.out.println("EVENTS " + changeEvents.get(0));

    }
}
