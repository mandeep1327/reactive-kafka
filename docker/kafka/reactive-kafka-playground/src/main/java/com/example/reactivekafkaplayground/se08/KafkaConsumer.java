package com.example.reactivekafkaplayground.se08;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.List;
import java.util.Map;


//goal:parallel processin with ordering
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
        var options = ReceiverOptions.<String,String>create(consumerConfig)
                .subscription(List.of("hello-world"));

        KafkaReceiver.create(options)
                .receive()
                .groupBy(r->Integer.parseInt(r.key())%5)
                //we can group by r.partition()
                //r.key().hashCode()%5
                .flatMap(KafkaConsumer::batchProcess)
                .subscribe();
    }

    private static Mono<Void> batchProcess(GroupedFlux<Integer,ReceiverRecord<String, String>> flux) {
        return flux.doFirst(() -> log.info("------------------mode value {}",flux.key()))
                .doOnNext(r -> log.info("Key {} value {}", r.key(), r.value()))
                .doOnNext(r->r.receiverOffset().acknowledge())
                .then(Mono.delay(Duration.ofSeconds(1)))
                .then();
    }
}

