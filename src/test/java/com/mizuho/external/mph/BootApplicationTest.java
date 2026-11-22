package com.mizuho.external.mph;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BootApplicationTest {

    @Test
    void rejectsStartupWithoutAnActiveProfile() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> BootApplication.requireActiveProfile(new MockEnvironment()));

        assertTrue(exception.getMessage().contains("No Spring profile is active"));
    }

    @Test
    void acceptsStartupWithAnActiveProfile() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("local");

        assertDoesNotThrow(() -> BootApplication.requireActiveProfile(environment));
    }
}
