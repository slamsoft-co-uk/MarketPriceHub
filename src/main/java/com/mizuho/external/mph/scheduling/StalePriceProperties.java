package com.mizuho.external.mph.scheduling;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "no.days.before.price.is")
public record StalePriceProperties(@Min(0) long stale) {
}
