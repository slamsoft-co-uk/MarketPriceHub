package com.mizuho.external.mph.dto.response;

import com.mizuho.external.mph.domain.dto.response.GetPricesForAllVendorsOfGivenInstrumentResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetPricesForAllVendorsOfGivenInstrumentResponseTest {

    @Test
    void storesVendorAndPriceDetails() {
        LocalDateTime priceDate = LocalDateTime.of(2026, 11, 21, 12, 30);
        GetPricesForAllVendorsOfGivenInstrumentResponse response = new GetPricesForAllVendorsOfGivenInstrumentResponse(
                "V001", "Vendor 1", new BigDecimal("123.4500"), priceDate);

        assertEquals("V001", response.vendorId());
        assertEquals("Vendor 1", response.vendorDescription());
        assertEquals(new BigDecimal("123.4500"), response.price());
        assertEquals(priceDate, response.priceDate());
    }

    @Test
    void hasValueBasedEqualityAndHashCode() {
        GetPricesForAllVendorsOfGivenInstrumentResponse response = new GetPricesForAllVendorsOfGivenInstrumentResponse(
                "V001", "Vendor 1", new BigDecimal("123.45"),
                LocalDateTime.of(2026, 11, 21, 12, 30));

        GetPricesForAllVendorsOfGivenInstrumentResponse equalResponse = new GetPricesForAllVendorsOfGivenInstrumentResponse(
                "V001", "Vendor 1", new BigDecimal("123.45"),
                LocalDateTime.of(2026, 11, 21, 12, 30));

        assertEquals(equalResponse, response);
        assertEquals(equalResponse.hashCode(), response.hashCode());
    }
}
