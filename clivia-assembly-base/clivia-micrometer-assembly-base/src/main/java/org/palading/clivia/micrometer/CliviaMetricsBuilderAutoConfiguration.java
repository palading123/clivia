package org.palading.clivia.micrometer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author palading_cr
 * @title CliviaMetricsBuilderAutoConfiguration
 * @project clivia
 */
@Configuration
public class CliviaMetricsBuilderAutoConfiguration {

    private static final String default_metrics_name = "clivia-metrics";

    @Bean
    @ConditionalOnMissingBean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(Environment environment) {
        return registry -> {
            registry.config().commonTags("clivia-monitor",
                environment.getProperty("spring.application.name", default_metrics_name));
        };
    }

    @Bean
    public CliviaCountMetricsBuilderFactory countMetricsBuilderFactory(ObjectProvider<MeterRegistry> meterRegistries) {
        return new CliviaCountMetricsBuilderFactory(meterRegistries.getIfAvailable(), "clivia.request.count");
    }

    @Bean
    public CliviaTimerMetricsBuilderFactory timerMetricsBuilderFactory(ObjectProvider<MeterRegistry> meterRegistries) {
        return new CliviaTimerMetricsBuilderFactory(meterRegistries.getIfAvailable(), "clivia.request.timer");
    }
}
