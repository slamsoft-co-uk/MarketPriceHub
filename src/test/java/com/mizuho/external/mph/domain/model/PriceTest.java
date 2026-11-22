package com.mizuho.external.mph.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PriceTest {

    @Test
    void storesVendorInstrumentAmountAndDate() {
        LocalDateTime priceDate = LocalDateTime.of(2026, 11, 21, 12, 30);
        Price price = new Price("V001", "I001", new BigDecimal("123.4500"), priceDate);

        assertEquals("V001", price.vendorId());
        assertEquals("I001", price.instrumentId());
        assertEquals(new BigDecimal("123.4500"), price.price());
        assertEquals(priceDate, price.priceDate());
    }

    @Test
    void hasValueBasedEqualityAndHashCode() {
        LocalDateTime priceDate = LocalDateTime.of(2026, 11, 21, 12, 30);
        Price price = new Price("V001", "I001", new BigDecimal("123.45"), priceDate);

        assertEquals(new Price("V001", "I001", new BigDecimal("123.45"), priceDate), price);
        assertEquals(new Price("V001", "I001", new BigDecimal("123.45"), priceDate).hashCode(),
                price.hashCode());
        assertNotEquals(new Price("V002", "I001", new BigDecimal("123.45"), priceDate), price);
        assertNotEquals(new Price("V001", "I002", new BigDecimal("123.45"), priceDate), price);
        assertNotEquals(new Price("V001", "I001", new BigDecimal("124.45"), priceDate), price);
        assertNotEquals(new Price("V001", "I001", new BigDecimal("123.45"), priceDate.plusSeconds(1)), price);
    }
}
