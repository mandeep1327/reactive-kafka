package net.apmoller.crb.microservices.external.apis.dcsa.processor.config;

import MSK.com.external.dcsa.DcsaTrackTraceEvent;
import MSK.com.gems.GEMSPubType;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * This class contains all the configuration information for Kafka producer factory to be
 * able to create a Kafka template for publishing messages
 */
@Configuration
@Getter
@Slf4j
public class ReactiveKafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.topic}")
    private String topicName;
    @Value("${kafka.consumer.username:}")
    private String username;
    @Value("${kafka.consumer.password:}")
    private String password;
    @Value("${kafka.client-id}")
    private String clientId;
    @Value("${kafka.consumer.group}")
    private String consumerGroup;

    @Value("${kafka.login-module:org.apache.kafka.common.security.plain.PlainLoginModule}")
    private String loginModule;
    @Value("${kafka.sasl-mechanism:PLAIN}")
    private String saslMechanism;
    @Value("${kafka.security-protocol:SASL_SSL}")
    private String securityProtocol;

    @Value("${kafka.consumer.offset-auto-reset:latest}")
    private String consumerOffsetAutoReset;
    @Value("${kafka.consumer.max-poll-records:20}")
    private String consumerMaxPollRecords;
    @Value("${kafka.consumer.concurrency:3}")
    private int consumerConcurrency;

    @Value("${kafka.consumer.retry.max-attempts:3}")
    private int maxRetryAttempts;
    @Value("${kafka.consumer.retry.initial-interval-secs:2}")
    private int retryInitialIntervalSeconds;
    @Value("${kafka.consumer.max-poll-timeout:5000}")
    private long pollTimeout;
    @Value("${kafka.consumer.max-fetch-size-bytes}")
    private Integer maxRequestSizeBytes;

    @Value("${kafka.schemaRegistry.url}")
    private String schemaRegistryUrl;
    @Value("${kafka.schemaRegistry.username}")
    private String schemaRegistryUsername;
    @Value("${kafka.schemaRegistry.password}")
    private String schemaRegistryPassword;


    @Bean
    KafkaReceiver<String, GEMSPubType> kafkaReceiver(ReceiverOptions<String, GEMSPubType> kafkaReceiverOptions) {
        return KafkaReceiver.create(kafkaReceiverOptions.pollTimeout(Duration.ofMillis(pollTimeout))
                .subscription(List.of(topicName))
                .addAssignListener(partitions -> log.info("Assigned Partitions {} on Thread named {} Id {}",
                                partitions, Thread.currentThread().getName(), Thread.currentThread().getId())));
    }

    @Bean
    ReceiverOptions<String, GEMSPubType> kafkaReceiverOptions() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, KafkaAvroDeserializer.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerOffsetAutoReset);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerMaxPollRecords);
        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxRequestSizeBytes);
        properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        properties.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        properties.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, schemaRegistryUsername + ":" + schemaRegistryPassword);
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        addSaslProperties(properties, saslMechanism, securityProtocol, loginModule, username, password);
        return ReceiverOptions.create(properties);
    }


    @Bean
    SenderOptions<String, DcsaTrackTraceEvent> kafkaSenderOptions() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "dcsa-processor-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        props.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, schemaRegistryUsername + ":" + schemaRegistryPassword);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        addSaslProperties(props, saslMechanism, securityProtocol, loginModule, username, password);
        return SenderOptions.create(props);

    }


    @Bean
    KafkaSender<String, DcsaTrackTraceEvent> kafkaSender(SenderOptions<String, DcsaTrackTraceEvent> kafkaSenderOptions) {
        return KafkaSender.create(kafkaSenderOptions);
    }


    private static void addSaslProperties(Map<String, Object> properties, String saslMechanism, String securityProtocol,
                                          String loginModule, String username, String password) {
        if (nonNull(username) && !username.isBlank()) {
            properties.put("security.protocol", securityProtocol);
            properties.put("sasl.mechanism", saslMechanism);
            var saslJassConfig = String.format("%s required username=\"%s\" password=\"%s\" ;", loginModule, username, password);
            properties.put("sasl.jaas.config", saslJassConfig);
        }
    }

}

