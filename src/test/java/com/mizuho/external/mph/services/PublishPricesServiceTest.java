package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryInstrumentRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryVendorRepository;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import com.mizuho.external.mph.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PublishPricesServiceTest {

    @Test
    void publishesPriceForVendorAndInstrument() {
        VendorRepository vendorRepository = new InMemoryVendorRepository();
        InstrumentRepository instrumentRepository = new InMemoryInstrumentRepository();
        PriceRepository priceRepository = new InMemoryPriceRepository();
        vendorRepository.save(new Vendor("V001", "Vendor 1"));
        instrumentRepository.save(new Instrument("I001", "Instrument 1"));
        PublishPricesService service =
                new PublishPricesService(vendorRepository, instrumentRepository, priceRepository);
        LocalDateTime priceDate = LocalDateTime.of(2026, 10, 15, 12, 30);

        service.publishPrice("V001", "I001", new BigDecimal("123.4501"), priceDate);

        Price price = priceRepository.findByVendorAndInstrument("V001", "I001").orElseThrow();
        assertEquals(new BigDecimal("123.4501"), price.price());
        assertEquals(priceDate, price.priceDate());
    }

    @Test
    void rejectsUnknownInstrumentOrVendor() {
        VendorRepository vendorRepository = new InMemoryVendorRepository();
        InstrumentRepository instrumentRepository = new InMemoryInstrumentRepository();
        PriceRepository priceRepository = new InMemoryPriceRepository();
        vendorRepository.save(new Vendor("V001", "Vendor 1"));
        instrumentRepository.save(new Instrument("I001", "Instrument 1"));
        PublishPricesService service =
                new PublishPricesService(vendorRepository, instrumentRepository, priceRepository);
        LocalDateTime priceDate = LocalDateTime.now();

        assertThrows(ResourceNotFoundException.class,
                () -> service.publishPrice("V001", "missing", BigDecimal.ONE, priceDate));
        assertThrows(ResourceNotFoundException.class,
                () -> service.publishPrice("missing", "I001", BigDecimal.ONE, priceDate));
    }
}
