package util;

import static java.util.Collections.singletonList;

import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestContainer {
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:5.4.3");
    public static EnvironmentReader environmentReader;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;
    private static ConsumerRecords<String, String> records;
    private static KafkaContainer kafka;

    public static KafkaContainer setupKafkaContainer() throws Exception {
        Network network = Network.newNetwork();

        environmentReader = new EnvironmentReader();

         kafka = new KafkaContainer(KAFKA_IMAGE);
//                 .withEnv("KAFKA_CLIENT_PORT", environmentReader.getKafkaPort())
//                .withEnv("KAFKA_ADVERTISED_LISTENERS",environmentReader.getKafkaPort());

         kafka.start();

        setupKafkaProducer();

        return kafka;

    }

    public static void setupKafkaProducer(){
        producer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ProducerConfig.CLIENT_ID_CONFIG,"external-dcsa-events-processor"),
                new StringSerializer(),
                new StringSerializer()
        );
    }

    public static void setupKafkaConsumer(){
        consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "1"),
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    protected static void setupConfig(int partitions, int rf) throws Exception {
/*        try (
                AdminClient adminClient = AdminClient.create(ImmutableMap.of(
                        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()
                ));
        ) {

            Collection<NewTopic> topics = singletonList(new NewTopic(environmentReader.getKafkaConsumerTopic(), partitions, (short) rf));
            adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS);
*/
            consumer.subscribe(singletonList(environmentReader.getKafkaPublisherTopic()));

        //}
    }

    protected static void sendToProducer(String content) {
        producer.send(new ProducerRecord<>(environmentReader.getKafkaConsumerTopic(), content));
        }


    protected static List<ConsumerRecord<String, String>> drain(
            int expectedRecordCount) {

        List<ConsumerRecord<String, String>> allRecords = new ArrayList<>();

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            consumer.poll(Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(allRecords::add);
            System.out.println("ALL RECORDS SIZE " + allRecords.size());
            return allRecords.size() == expectedRecordCount;
        });

        return allRecords;
    }

    public static String getBootstrapServers() {
        return kafka.getBootstrapServers();
    }
}
