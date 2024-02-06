package com.example.reactivekafkaplayground.se05;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;


//cluster demo
@Slf4j
public class KafkaConsumer {
    public static void main(String[] instanceid) {

      var consumerConfig=  Map.<String,Object>of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:8081",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG,"demo-group-123",
              ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest",
              ConsumerConfig.GROUP_INSTANCE_ID_CONFIG,instanceid
                );
      var options=    ReceiverOptions.create(consumerConfig).subscription(List.of("order-events"));

     KafkaReceiver.create(options)
               .receive()
               .doOnNext(r->log.info("key {}, value {}",r.key(),r.value()))
             .doOnNext(r->r.receiverOffset().acknowledge())
               .subscribe();
    }
}

