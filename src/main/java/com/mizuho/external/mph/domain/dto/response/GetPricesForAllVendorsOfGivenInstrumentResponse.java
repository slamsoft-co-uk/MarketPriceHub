package com.mizuho.external.mph.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record GetPricesForAllVendorsOfGivenInstrumentResponse(
        @JsonProperty("VendorId") String vendorId,
        @JsonProperty("VendorDescription") String vendorDescription,
        @JsonProperty("Price") BigDecimal price,
        @JsonProperty("PriceDate") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime priceDate) {}
