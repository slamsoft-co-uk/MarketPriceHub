package com.mizuho.external.mph.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Price(String vendorId, String instrumentId, BigDecimal price, LocalDateTime priceDate) {}
