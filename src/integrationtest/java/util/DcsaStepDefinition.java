package util;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

public class DcsaStepDefinition extends CucumberSpringConfiguration {
    @Given("place holder")
    public void iHelloWorld() {
        System.out.println("placeholder");
    }

    @Then("placeholder")
    public void trueIsTrue() {
        Assert.assertTrue(true);
    }
}
