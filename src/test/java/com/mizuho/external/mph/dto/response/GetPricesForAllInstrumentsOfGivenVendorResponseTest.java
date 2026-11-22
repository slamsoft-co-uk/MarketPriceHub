package com.mizuho.external.mph.dto.response;

import com.mizuho.external.mph.domain.dto.response.GetPricesForAllInstrumentsOfGivenVendorResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetPricesForAllInstrumentsOfGivenVendorResponseTest {

    @Test
    void storesInstrumentAndPriceDetails() {
        LocalDateTime priceDate = LocalDateTime.of(2026, 11, 21, 12, 30);
        GetPricesForAllInstrumentsOfGivenVendorResponse response = new GetPricesForAllInstrumentsOfGivenVendorResponse(
                "I001", "Instrument 1", new BigDecimal("123.4500"), priceDate);

        assertEquals("I001", response.instrumentId());
        assertEquals("Instrument 1", response.instrumentDescription());
        assertEquals(new BigDecimal("123.4500"), response.price());
        assertEquals(priceDate, response.priceDate());
    }

    @Test
    void hasValueBasedEqualityAndHashCode() {
        GetPricesForAllInstrumentsOfGivenVendorResponse response = new GetPricesForAllInstrumentsOfGivenVendorResponse(
                "I001", "Instrument 1", new BigDecimal("123.45"),
                LocalDateTime.of(2026, 11, 21, 12, 30));

        GetPricesForAllInstrumentsOfGivenVendorResponse equalResponse = new GetPricesForAllInstrumentsOfGivenVendorResponse(
                "I001", "Instrument 1", new BigDecimal("123.45"),
                LocalDateTime.of(2026, 11, 21, 12, 30));

        assertEquals(equalResponse, response);
        assertEquals(equalResponse.hashCode(), response.hashCode());
    }
}
