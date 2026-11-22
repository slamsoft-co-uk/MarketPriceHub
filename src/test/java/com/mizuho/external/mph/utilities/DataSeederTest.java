package com.mizuho.external.mph.utilities;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryInstrumentRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryVendorRepository;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSeederTest {

    @Test
    void runLoadsDemoVendorsInstrumentsAndPrices() {
        InstrumentRepository instrumentRepository = new InMemoryInstrumentRepository();
        VendorRepository vendorRepository = new InMemoryVendorRepository();
        PriceRepository priceRepository = new InMemoryPriceRepository();
        DataSeeder seeder = new DataSeeder(instrumentRepository, vendorRepository, priceRepository);

        LocalDateTime beforeLoading = LocalDateTime.now();
        seeder.run(new DefaultApplicationArguments());
        LocalDateTime afterLoading = LocalDateTime.now();

        assertEquals("Bloomberg", vendorRepository.findById("ve01").orElseThrow().name());
        assertTrue(vendorRepository.findById("ve08").isPresent());
        assertTrue(vendorRepository.findById("ve03").isPresent());
        assertEquals("US 10Y Treasury", instrumentRepository.findById("in01").orElseThrow().name());
        assertTrue(instrumentRepository.findById("in12").isPresent());

        List<Price> vendorPrices = priceRepository.findByVendorId("ve01");
        assertEquals(12, vendorPrices.size());
        assertEquals(84, Stream.of("ve01", "ve02", "ve04", "ve05", "ve06", "ve07", "ve08")
                .mapToInt(vendorId -> priceRepository.findByVendorId(vendorId).size())
                .sum());
        assertEquals(7, priceRepository.findByInstrumentId("in01").size());
        assertTrue(priceRepository.findByVendorId("ve03").isEmpty());

        Price currentDatePrice = priceRepository.findByVendorAndInstrument("ve01", "in01").orElseThrow();
        assertEquals("99.50", currentDatePrice.price().toPlainString());
        assertFalse(currentDatePrice.priceDate().isBefore(beforeLoading));
        assertFalse(currentDatePrice.priceDate().isAfter(afterLoading));
        assertEquals(LocalDateTime.parse("2025-11-21T17:42:12"),
                priceRepository.findByVendorAndInstrument("ve01", "in03").orElseThrow().priceDate());
    }
}
