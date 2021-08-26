package util;

import MSK.com.gems.GEMSPubType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public class DcsaStepDefinition extends CucumberSpringConfiguration {

    @Given("set a message on kafka with {string}")
    public void send(String jsonfile) throws Exception {
        var jsonContent = TestDataUtils.loadDataJson(jsonfile);
        KafkaTestContainer.sendToProducer(jsonContent);
    }

    @Then("the EMPv2 topic should produce a message")
    public void theEMPvTopicShouldProduceAMessage() throws Exception {
        List<ConsumerRecord<String, GEMSPubType>> changeEvents =
                KafkaTestContainer.drain( 1);
        System.out.println("EVENTS " + changeEvents.get(0));

    }
}
