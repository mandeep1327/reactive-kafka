package util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Data
public class EnvironmentReader {
    private String ena;
    private String kafkaBootsrapServers;
    private String kafkaConsumerTopic;
    private String kafkaConsumerGroup;
    private String kafkaSchemaRegistryUrl;
    private String kafkaPort;
    private String kafkaPublisherTopic;
    private String kafkaBootstrapPort;

    public EnvironmentReader() {
        try (InputStream input = new FileInputStream("src/integrationtest/resources/testConfiguration.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.kafkaBootsrapServers = prop.getProperty("KAFKA_BOOTSTRAP_SERVERS");
            this.kafkaConsumerTopic = prop.getProperty("KAFKA_CONSUMER_TOPIC");
            this.kafkaConsumerGroup = prop.getProperty("KAFKA_CONSUMER_GROUP");
            this.kafkaSchemaRegistryUrl = prop.getProperty("KAFKA_SCHEMA_REGISTRY_URL");
            this.kafkaPort = prop.getProperty("KAKFA_PORT");
            this.kafkaPublisherTopic = prop.getProperty("KAFKA_PUBLISHER_TOPIC");
            this.kafkaBootstrapPort = prop.getProperty("KAKFA_BOOTSTRAP_PORT");

        } catch (IOException e) {
            log.error("Exception: " + e);
        }
    }
}