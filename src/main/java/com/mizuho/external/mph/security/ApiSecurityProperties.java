package com.mizuho.external.mph.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "security.api")
public record ApiSecurityProperties(
        @NotBlank String username,
        @NotBlank @Size(min = 16, max = 72) String password) {
}
