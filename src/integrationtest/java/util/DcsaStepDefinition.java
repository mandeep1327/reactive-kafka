package util;

import MSK.com.gems.GEMSPubType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class DcsaStepDefinition extends CucumberSpringConfiguration {
    private String jsonContent;

    @Given("set a message on kafka with {string}")
    public void send(String jsonfile) throws Exception {
        jsonContent = TestDataUtils.loadDataJson(jsonfile);
        KafkaTestContainer.sendToProducer(jsonContent);
    }

    @And("I wait external-dcsa-events-processor consumes the message")
    public void iWaitExternalDcsaEventsProcessorConsumesTheMessage() throws InterruptedException {
        await().atMost(30, TimeUnit.SECONDS).until(() -> false);
    }

    @Then("the EMPv2 topic should produce a message")
    public void theEMPvTopicShouldProduceAMessage() throws Exception {
        KafkaTestContainer.setupKafkaConsumer();
        KafkaTestContainer.setupConfig( 1, 1);

        List<ConsumerRecord<String, GEMSPubType>> changeEvents =
                KafkaTestContainer.drain( 1);
        System.out.println("EVENTS " + changeEvents.get(0));

    }

    @Given("Nothing given")
    public void nothing() {

    }
    @Then("Nothing")
    public void thenNothing() {
    }
}
