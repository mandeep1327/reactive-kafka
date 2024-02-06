package com.example.reactivekafkaplayground.se09;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;


//goal:error handling
@Slf4j
public class KafkaConsumer {
    public static void main(String[] instanceid) {

        var consumerConfig = Map.<String, Object>of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, "demo-group-123",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "1",
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3
        );
        var options = ReceiverOptions.<String, String>create(consumerConfig)
                .subscription(List.of("hello-world"));

        KafkaReceiver.create(options)
                .receive()
                .log()
                .doOnNext(r -> log.info("Key {} value {}", r.key(), r.value().toString().toCharArray()[15]))     //just for demo
                .doOnError(ex -> log.error(ex.getMessage()))
                .doOnNext(r -> r.receiverOffset().acknowledge())
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .subscribe();
    }

}

