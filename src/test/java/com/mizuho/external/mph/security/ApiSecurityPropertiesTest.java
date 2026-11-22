package com.mizuho.external.mph.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiSecurityPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class))
            .withUserConfiguration(PropertiesConfiguration.class);

    @Test
    void acceptsValidCredentialsAndPasswordLengthBoundaries() {
        contextRunner
                .withPropertyValues("security.api.username=test-user", "security.api.password=1234567890123456")
                .run(context -> {
                    assertNull(context.getStartupFailure());
                    assertNotNull(context.getBean(ApiSecurityProperties.class));
                });

        contextRunner
                .withPropertyValues("security.api.username=test-user",
                        "security.api.password=123456789012345678901234567890123456789012345678901234567890123456789012")
                .run(context -> {
                    assertNull(context.getStartupFailure());
                    assertNotNull(context.getBean(ApiSecurityProperties.class));
                });
    }

    @Test
    void rejectsBlankUsername() {
        contextRunner
                .withPropertyValues("security.api.username= ", "security.api.password=1234567890123456")
                .run(context -> assertNotNull(context.getStartupFailure()));
    }

    @Test
    void rejectsBlankOrTooShortPassword() {
        contextRunner
                .withPropertyValues("security.api.username=test-user", "security.api.password= ")
                .run(context -> assertNotNull(context.getStartupFailure()));

        contextRunner
                .withPropertyValues("security.api.username=test-user", "security.api.password=123456789012345")
                .run(context -> assertNotNull(context.getStartupFailure()));
    }

    @Test
    void rejectsPasswordLongerThan72Characters() {
        contextRunner
                .withPropertyValues("security.api.username=test-user",
                        "security.api.password=1234567890123456789012345678901234567890123456789012345678901234567890123")
                .run(context -> assertNotNull(context.getStartupFailure()));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(ApiSecurityProperties.class)
    static class PropertiesConfiguration {
    }
}
