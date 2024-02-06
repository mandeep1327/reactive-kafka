package com.example.reactivekafkaplayground.se01;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;

import java.util.Map;

@Slf4j
public class Lec01KafkaProducer {
    public static void main(String[] args) {
      var mapConfig= Map.<String,Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092",
               ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class,
               ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);

       var senderOptions= SenderOptions.<String,String>create(mapConfig);
        var senderRecord=  Flux.range(1,100)
                          .map(i->new ProducerRecord<>("order-events",i.toString(),"order-"+i))
                                  .map(pr->SenderRecord.create(pr,pr.key()));

        KafkaSender.create(senderOptions)
                .send(senderRecord)
                .doOnNext(r->log.info("correlation {}",r.correlationMetadata()))
                .subscribe();
    }

}
