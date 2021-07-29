package net.apmoller.crb.microservices.external.apis.dcsa.processor.config;

import java.util.Collection;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;

@RequiredArgsConstructor
public class ReactiveKafkaHealthIndicator extends AbstractReactiveHealthIndicator {

    private final AdminClient adminClient;

    @Override
    protected Mono<Health> doHealthCheck(Health.Builder builder) {
        final DescribeClusterOptions describeClusterOptions = new DescribeClusterOptions().timeoutMs(1000);
        DescribeClusterResult describeClusterResult = adminClient.describeCluster(describeClusterOptions);
        var clusterId = kafkaFutureToMono(describeClusterResult.clusterId());
        var nodeCount = kafkaFutureToMono(describeClusterResult.nodes()).map(Collection::size);
        var topics = kafkaFutureToMono(adminClient.listTopics().names());
        return Mono.zip(clusterId, nodeCount, topics)
                .map(result -> buildHealthResultUp(builder, result.getT1(), result.getT2(), result.getT3()))
                .onErrorResume(ex -> Mono.just(buildHealthResultDown(builder, ex)));
    }

    private <T> Mono<T> kafkaFutureToMono(KafkaFuture<T> kafkaFuture) {
        return Mono.fromCallable(kafkaFuture::get).subscribeOn(Schedulers.boundedElastic());
    }

    private Health buildHealthResultUp(Health.Builder builder, String clusterId, Integer nodeCount, Set<String> topics) {
        return builder.up()
                .withDetail("clusterId", clusterId)
                .withDetail("nodeCount", nodeCount)
                .withDetail("topics", topics).build();
    }

    private Health buildHealthResultDown(Health.Builder builder, Throwable ex) {
        return builder.down()
                .withException(ex)
                .build();
    }
}
