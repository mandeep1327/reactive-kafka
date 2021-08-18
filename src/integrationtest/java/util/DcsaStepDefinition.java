package util;

import io.cucumber.java.en.Given;
import org.junit.jupiter.api.BeforeAll;

public class DcsaStepDefinition extends CucumberSpringConfiguration {

    @BeforeAll
    public static void setup() throws Exception {
        System.out.println("GOT");
    }

    @Given("set a message on kafka")
    public void send() throws Exception {
        KafkaTestContainer.setupKafkaContainer();

        /// KafkaTestContainer.sendToProducer("Test", "test");
    }
}
