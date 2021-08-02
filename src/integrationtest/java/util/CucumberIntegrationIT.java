package util;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationtest/resources/features",
        plugin = {"pretty", "junit:target/cucumber-reports/Cucumber.xml"})
public class CucumberIntegrationIT {

    @BeforeClass
    public static void setupClass() {
        EnvironmentReader environmentReader = new EnvironmentReader();

    }
}
