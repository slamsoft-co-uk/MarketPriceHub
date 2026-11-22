package com.mizuho.external.mph.dto.request;

import com.mizuho.external.mph.domain.dto.request.PublishPricesRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PublishPricesRequestTest {

    @Test
    public void testObjectPopulatesCorrectly() {

        String instrumentId = "In001";
        String vendorId = "V001";
        BigDecimal price = new BigDecimal("12345.7821");
        LocalDateTime priceDate = LocalDateTime.now();

        PublishPricesRequest publishPricesRequest = new PublishPricesRequest(instrumentId, vendorId, price, priceDate);

        assertEquals(instrumentId, publishPricesRequest.getInstrumentId());
        assertEquals(vendorId, publishPricesRequest.getVendorId());
        assertEquals(price, publishPricesRequest.getPrice());
        assertEquals(priceDate, publishPricesRequest.getPriceDate());

    }

}
