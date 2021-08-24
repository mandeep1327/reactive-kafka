package util;

import io.cucumber.spring.CucumberContextConfiguration;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.DcsaEventProcessorApplication;
import org.junit.ClassRule;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@ActiveProfiles(value = "integrationtest")
public class CucumberSpringConfiguration {

}
