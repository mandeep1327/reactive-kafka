package com.example.reactivekafkaplayground.se09;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


//goal:error handling :seperate receiver and process pipeline
@Slf4j
public class KafkaConsumerV2 {
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
                .concatMap(KafkaConsumerV2::process)
                .subscribe();
    }

    private static Mono<Void> process(ReceiverRecord<String, String> receiverRecord) {
        return Mono.just(receiverRecord)
                .doOnNext(r -> {
                    if(r.key().equals("5"))
                        throw new RuntimeException("db is down");
                    var index = ThreadLocalRandom.current().nextInt(1, 20);
                    log.info("Key {} index {},value {}", r.key(),index, r.value().toString().toCharArray()[index]);
                    r.receiverOffset().acknowledge();
                })
                .retryWhen(retrySpec())
                .doOnError(ex -> log.error(ex.getMessage()))
                .onErrorResume(IndexOutOfBoundsException.class,ex->Mono.fromRunnable(()->receiverRecord.receiverOffset().acknowledge()))
                .then();
    }
    private static Retry retrySpec() {
        return Retry.fixedDelay(3,Duration.ofSeconds(1))
                .filter(IndexOutOfBoundsException.class::isInstance)
                .onRetryExhaustedThrow((spec,signal)->signal.failure());
    }
}

