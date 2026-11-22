package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryVendorRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetPricesForAllVendorsOfGivenInstrumentResponseServiceTest {

    @Test
    void getsAllVendorsAndPricesForInstrument() {
        VendorRepository vendorRepository = new InMemoryVendorRepository();
        PriceRepository priceRepository = new InMemoryPriceRepository();
        Vendor vendor1 = new Vendor("V001", "Vendor 1");
        Vendor vendor2 = new Vendor("V002", "Vendor 2");
        vendorRepository.save(vendor1);
        vendorRepository.save(vendor2);
        LocalDateTime priceDate = LocalDateTime.now();
        priceRepository.save(new Price("V001", "I001", new BigDecimal("10.25"), priceDate));
        priceRepository.save(new Price("V002", "I001", new BigDecimal("9.75"), priceDate));

        GetPricesForAllVendorsOfGivenInstrumentService service =
                new GetPricesForAllVendorsOfGivenInstrumentService(priceRepository, vendorRepository);

        assertEquals(Map.of(
                vendor1, new Price("V001", "I001", new BigDecimal("10.25"), priceDate),
                vendor2, new Price("V002", "I001", new BigDecimal("9.75"), priceDate)),
                service.getPrices("I001"));
    }

    @Test
    void returnsEmptyMapWhenInstrumentHasNoPrices() {
        GetPricesForAllVendorsOfGivenInstrumentService service =
                new GetPricesForAllVendorsOfGivenInstrumentService(new InMemoryPriceRepository(), new InMemoryVendorRepository());

        assertEquals(Map.of(), service.getPrices("unknown"));
    }
}
