package util;

import static java.util.Collections.singletonList;

import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestContainer {
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:5.4.3");
    private static final String topicName = "MSK.shipment.test.miscellaneousEvents.topic.internal.any.v1";
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;

    public static void setupKafkaContainer() throws Exception {
        KafkaContainer kafka = new KafkaContainer(KAFKA_IMAGE).withEnv("KAFKA_CLIENT_PORT", "8081");
        setupKafkaProducer(kafka);
        setupKafkaConsumer(kafka);
        kafka.start();
        setupConfig(kafka.getBootstrapServers(), 1, 1);
        producer.send(new ProducerRecord<>(topicName, "resr", "rsr"));
    }

    public static void setupKafkaProducer(KafkaContainer kafka){
        producer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                ),
                new StringSerializer(),
                new StringSerializer()
        );
    }

    public static void setupKafkaConsumer(KafkaContainer kafka){
        consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "MSK.external.dcsa.consumerGroup.v1"),
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    protected static void setupConfig(String bootstrapServers, int partitions, int rf) throws Exception {
        try (
                AdminClient adminClient = AdminClient.create(ImmutableMap.of(
                        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
                ));
        ) {

            Collection<NewTopic> topics = singletonList(new NewTopic(topicName, partitions, (short) rf));
            adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS);

            consumer.subscribe(singletonList(topicName));

        }
    }

    protected static void sendToProducer(String key, String value) throws Exception {

        producer.send(new ProducerRecord<>(topicName, key, value));
        }
    }