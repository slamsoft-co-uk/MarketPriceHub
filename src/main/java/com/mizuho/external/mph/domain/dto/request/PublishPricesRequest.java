package com.mizuho.external.mph.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PublishPricesRequest(
        @NotBlank @Size(max = 20)
        String instrumentId,
        @NotBlank @Size(max = 20)
        String vendorId,
        @NotNull
        @DecimalMin(value = "0", inclusive = false)
        @Digits(integer = 15, fraction = 4)
        BigDecimal price,
        @NotNull
        LocalDateTime priceDate
) {

    public String getInstrumentId() {
        return instrumentId;
    }
    public LocalDateTime getPriceDate() {
        return priceDate;
    }
    public String getVendorId() {
        return vendorId;
    }
    public BigDecimal getPrice() {
        return price;
    }
}
