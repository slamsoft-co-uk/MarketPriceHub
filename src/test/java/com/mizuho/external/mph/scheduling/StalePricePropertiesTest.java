package com.mizuho.external.mph.scheduling;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class StalePricePropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(PropertiesConfiguration.class);

    @Test
    void acceptsNonNegativeStalePriceAge() {
        contextRunner.withPropertyValues("no.days.before.price.is.stale=0").run(context -> {
            assertNull(context.getStartupFailure());
            assertNotNull(context.getBean(StalePriceProperties.class));
        });
    }

    @Test
    void rejectsNegativeStalePriceAgeAtStartup() {
        contextRunner.withPropertyValues("no.days.before.price.is.stale=-1").run(context ->
                assertNotNull(context.getStartupFailure()));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(StalePriceProperties.class)
    static class PropertiesConfiguration {
    }
}
