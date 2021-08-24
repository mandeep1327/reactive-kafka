package util;

import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import MSK.com.gems.GEMSPubType;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import net.apmoller.crb.microservices.external.apis.dcsa.processor.DcsaEventProcessorApplication;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationtest/resources/features",
        plugin = {"pretty", "junit:target/cucumber-reports/Cucumber.xml"})
@Import(CucumberIntegrationIT.KafkaTestContainerConfiguration.class)
@SpringBootTest(classes = DcsaEventProcessorApplication.class)
public class CucumberIntegrationIT {

    @ClassRule
    public static KafkaContainer startKafka() throws Exception {
         return KafkaTestContainer.setupKafkaContainer();
    }

//    @ClassRule
//    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @TestConfiguration
    static class KafkaTestContainerConfiguration {

        @Bean
        SenderOptions<String, DcsaTrackTraceEvent> kafkaSenderOptions() {

            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainer.getBootstrapServers());
            props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            return SenderOptions.create(props);

        }

        @Bean
        ReceiverOptions<String, GEMSPubType> kafkaReceiverOptions() {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainer.getBootstrapServers());
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
            properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            return ReceiverOptions.create(properties);
        }
    }
}