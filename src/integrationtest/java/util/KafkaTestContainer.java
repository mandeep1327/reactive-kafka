package util;

import MSK.com.gems.GEMSPubType;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

public class KafkaTestContainer {
    private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:5.4.3");
    private static KafkaProducer<String, GEMSPubType> producer;
    private static KafkaConsumer<String, GEMSPubType> consumer;
    private static KafkaContainer kafka;

    public static KafkaContainer setupKafkaContainer() {
        kafka = new KafkaContainer(KAFKA_IMAGE);
        kafka.start();
        setupKafkaProducer();
        setupKafkaConsumer();
        setupConfig();
        return kafka;
    }

    public static void setupKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "external-dcsa-events-processor");
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producer = new KafkaProducer<>(props);
    }

    public static void setupKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "mock://testUrl");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkatest");
        consumer = new KafkaConsumer<>(props);
    }

    protected static void setupConfig() {
        consumer.subscribe(singletonList(EnvironmentReader.getInstance().getKafkaPublisherTopic()));
    }

    protected static void sendToProducer(String content) {
        GEMSPubType gemContent = TestDataUtils.getGemsData(content);
        producer.send(new ProducerRecord<>(EnvironmentReader.getInstance().getKafkaConsumerTopic(), gemContent));
    }


    protected static List<ConsumerRecord<String, GEMSPubType>> drain(int expectedRecordCount) {

        List<ConsumerRecord<String, GEMSPubType>> allRecords = new ArrayList<>();

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            consumer.poll(Duration.ofMillis(50))
                    .iterator()
                    .forEachRemaining(allRecords::add);
            return allRecords.size() == expectedRecordCount;
        });

        return allRecords;
    }

    public static String getBootstrapServers() {
        return kafka.getBootstrapServers();
    }
}
