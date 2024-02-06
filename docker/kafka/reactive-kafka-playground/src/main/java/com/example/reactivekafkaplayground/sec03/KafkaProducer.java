package com.example.reactivekafkaplayground.sec03;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.time.Duration;
import java.util.Map;

@Slf4j
public class KafkaProducer {
    public static void main(String[] args) {
        var producerConfig = Map.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );
        var options = SenderOptions.<String, String>create(producerConfig);
        // .maxInFlight(100);
        var senderResult = Flux.interval(Duration.ofMillis(50))
                .take(10000)
                .map(i-> new ProducerRecord<>("order-events", i.toString(), "order-" + i))
                .map(pr->SenderRecord.create(pr, pr.key()))
                ;
        KafkaSender.create(options)
                .send(senderResult)
                .doOnNext(r -> log.info("correlation id {}", r.correlationMetadata()))
                .subscribe();

    }

    private static SenderRecord<String, String, String> crateSenderRecord(Integer i) {
        var headers = new RecordHeaders();
        headers.add("client-id", "some-client".getBytes());
        headers.add("tracing-id", "123".getBytes());
        var producerRecord = new ProducerRecord<>("order-events", null, i.toString(), "order-" + i, headers);
        return SenderRecord.create(producerRecord, producerRecord.key());
    }
}
