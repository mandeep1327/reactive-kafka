package util;

import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import MSK.com.gems.GEMSPubType;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/integrationtest/resources/features",
        plugin = {"pretty", "junit:target/cucumber-reports/Cucumber.xml"})
@Slf4j
public class CucumberIntegrationIT {

    @ClassRule
    public static KafkaContainer startKafka() {
         return KafkaTestContainer.setupKafkaContainer();
    }

    @TestConfiguration
    static class KafkaTestContainerConfiguration {

        @Bean
        KafkaReceiver<String, GEMSPubType> kafkaReceiver(ReceiverOptions<String, GEMSPubType> kafkaReceiverOptions) {
            return KafkaReceiver.create(kafkaReceiverOptions.pollTimeout(Duration.ofMillis(5000))
                    .subscription(List.of(EnvironmentReader.getInstance().getKafkaConsumerTopic()))
                    .addAssignListener(partitions -> log.info("Assigned Partitions {} on Thread named {} Id {}",
                    partitions, Thread.currentThread().getName(), Thread.currentThread().getId())));
        }

        @Bean
        KafkaSender<String, DcsaTrackTraceEvent> kafkaSender(SenderOptions<String, DcsaTrackTraceEvent> kafkaSenderOptions) {
            return KafkaSender.create(kafkaSenderOptions);
        }

        @Bean
        SenderOptions<String, DcsaTrackTraceEvent> kafkaSenderOptions() {

            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainer.getBootstrapServers());
            props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
            props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
            return SenderOptions.create(props);

        }

        @Bean
        ReceiverOptions<String, GEMSPubType> kafkaReceiverOptions() {
            Map<String, Object> properties = new HashMap<>();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaTestContainer.getBootstrapServers());
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
            properties.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
            properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, EnvironmentReader.getInstance().getKafkaConsumerTopic());
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return ReceiverOptions.create(properties);
        }
    }
}