package com.mizuho.external.mph.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetPricesForAllInstrumentsOfGivenVendorResponse(
        @JsonProperty("InstrumentId") String instrumentId,
        @JsonProperty("InstrumentDescription") String instrumentDescription,
        @JsonProperty("Price") BigDecimal price,
        @JsonProperty("PriceDate") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime priceDate) {}
