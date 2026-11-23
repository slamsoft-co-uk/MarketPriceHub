package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryInstrumentRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryVendorRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetAllPricesServiceTest {

    @Test
    void joinsAllPricesWithVendorAndInstrumentDetails() {
        InMemoryVendorRepository vendorRepository = new InMemoryVendorRepository();
        InMemoryInstrumentRepository instrumentRepository = new InMemoryInstrumentRepository();
        InMemoryPriceRepository priceRepository = new InMemoryPriceRepository();
        LocalDateTime priceDate = LocalDateTime.of(2026, 11, 21, 12, 30);

        vendorRepository.save(new Vendor("V001", "Vendor 1"));
        instrumentRepository.save(new Instrument("I001", "Instrument 1"));
        priceRepository.save(new Price("V001", "I001", new BigDecimal("10.25"), priceDate));

        GetAllPricesService service =
                new GetAllPricesService(priceRepository, vendorRepository, instrumentRepository);

        assertEquals(1, service.getAllPrices().size());
        assertEquals("V001", service.getAllPrices().getFirst().vendorId());
        assertEquals("Vendor 1", service.getAllPrices().getFirst().vendorDescription());
        assertEquals("I001", service.getAllPrices().getFirst().instrumentId());
        assertEquals("Instrument 1", service.getAllPrices().getFirst().instrumentDescription());
        assertEquals(new BigDecimal("10.25"), service.getAllPrices().getFirst().price());
        assertEquals(priceDate, service.getAllPrices().getFirst().priceDate());
    }

    @Test
    void omitsPricesWhoseVendorOrInstrumentReferenceIsMissing() {
        InMemoryVendorRepository vendorRepository = new InMemoryVendorRepository();
        InMemoryInstrumentRepository instrumentRepository = new InMemoryInstrumentRepository();
        InMemoryPriceRepository priceRepository = new InMemoryPriceRepository();

        vendorRepository.save(new Vendor("V001", "Vendor 1"));
        instrumentRepository.save(new Instrument("I001", "Instrument 1"));
        priceRepository.save(new Price("missing-vendor", "I001", BigDecimal.ONE, LocalDateTime.now()));
        priceRepository.save(new Price("V001", "missing-instrument", BigDecimal.TEN, LocalDateTime.now()));

        GetAllPricesService service =
                new GetAllPricesService(priceRepository, vendorRepository, instrumentRepository);

        assertTrue(service.getAllPrices().isEmpty());
    }
}
