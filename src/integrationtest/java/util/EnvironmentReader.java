package util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.util.Objects.isNull;

@Slf4j
@Data
public class EnvironmentReader {

    private String kafkaConsumerTopic;
    private String kafkaConsumerGroup;
    private String kafkaPublisherTopic;
    private static EnvironmentReader self = null;

    public static EnvironmentReader getInstance() {
        if (isNull(self)) {
            self = new EnvironmentReader();
        }
        return self;
    }

    private EnvironmentReader() {
        try (InputStream input = new FileInputStream("src/integrationtest/resources/testConfiguration.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            this.kafkaConsumerTopic = prop.getProperty("KAFKA_CONSUMER_TOPIC");
            this.kafkaConsumerGroup = prop.getProperty("KAFKA_CONSUMER_GROUP");
            this.kafkaPublisherTopic = prop.getProperty("KAFKA_PUBLISHER_TOPIC");

        } catch (IOException e) {
            log.error("Exception: " + e);
        }
    }
}