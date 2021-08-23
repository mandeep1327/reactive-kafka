package net.apmoller.crb.microservices.external.apis.dcsa.processor.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Configuration
@RequiredArgsConstructor
public class ReactiveKafkaMonitoringConfig {

    @Value("${kafka.receiver.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.login-module:org.apache.kafka.common.security.plain.PlainLoginModule}")
    private String loginModule;
    @Value("${kafka.sasl-mechanism:PLAIN}")
    private String saslMechanism;
    @Value("${kafka.security-protocol:SASL_SSL}")
    private String securityProtocol;

    @Value("${kafka.consumer.username:}")
    private String username;
    @Value("${kafka.consumer.password:}")
    private String password;
    @Value("${kafka.client-id}")
    private String clientId;

    @Bean
    @ConditionalOnProperty(value = "management.health.kafka.enabled", havingValue = "true")
    public ReactiveHealthIndicator kafkaHealthIndicator(AdminClient adminClient) {
        return new ReactiveKafkaHealthIndicator(adminClient);
    }

    @Bean
    @ConditionalOnProperty(value = "management.health.kafka.enabled", havingValue = "true")
    public AdminClient kafkaAdminClient() {
        return AdminClient.create(getKafkaAdmin(clientId, username, password).getConfigurationProperties());
    }

    private KafkaAdmin getKafkaAdmin(String clientId, String username, String password) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        addSaslProperties(properties, saslMechanism, securityProtocol, loginModule, username, password);
        return new KafkaAdmin(properties);
    }

    private static void addSaslProperties(Map<String, Object> properties, String saslMechanism, String securityProtocol, String loginModule, String username, String password) {
        if (nonNull(username) && !username.isBlank()) {
            properties.put("security.protocol", securityProtocol);
            properties.put("sasl.mechanism", saslMechanism);
            String saslJassConfig = String.format("%s required username=\"%s\" password=\"%s\" ;", loginModule, username, password);
            properties.put("sasl.jaas.config", saslJassConfig);
        }
    }

}

