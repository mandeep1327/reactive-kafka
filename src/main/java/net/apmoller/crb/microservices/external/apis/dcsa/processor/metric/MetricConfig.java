package net.apmoller.crb.microservices.external.apis.dcsa.processor.metric;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.config.validate.PropertyValidator;
import io.micrometer.core.ipc.http.HttpUrlConnectionSender;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Predicate;

import static net.apmoller.crb.microservices.external.apis.dcsa.processor.metric.MetricUtils.ENV_TAG_NAME;

@Configuration
@Setter
public class MetricConfig {

    @Value("${environment:}")
    private String environment;

    @Bean
    @ConditionalOnProperty(prefix = "management.metrics.export.datadog", name = "enabled", havingValue = "true", matchIfMissing = true)
    public DatadogMeterRegistry datadogMeterRegistry(DatadogConfig config, Clock clock) {
        var httpSender = getHttpSender(config);
        var meterRegistry = DatadogMeterRegistry.builder(config).clock(clock).httpClient(httpSender).build();
        addEnvCommonTag(meterRegistry);
        return meterRegistry;
    }

    private HttpUrlConnectionSender getHttpSender(DatadogConfig config) {
        return new HttpUrlConnectionSender(getConnectTimeout(config, "connectTimeout", 1),
                getConnectTimeout(config, "readTimeout", 10));
    }

    private Duration getConnectTimeout(DatadogConfig config, String timeoutPropertyName, int defaultDurationInSeconds) {
        return PropertyValidator.getDuration(config, timeoutPropertyName)
                .orElse(Duration.ofSeconds(defaultDurationInSeconds));
    }

    private void addEnvCommonTag(DatadogMeterRegistry meterRegistry) {
        Optional.ofNullable(environment)
                .filter(Predicate.not(String::isEmpty))
                .ifPresent(env -> meterRegistry.config().commonTags(ENV_TAG_NAME, env));
    }
}
