package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryInstrumentRepository;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetPricesForAllInstrumentsOfGivenVendorResponseServiceTest {

    @Test
    void getsAllInstrumentsAndPricesForVendor() {
        InstrumentRepository instrumentRepository = new InMemoryInstrumentRepository();
        PriceRepository priceRepository = new InMemoryPriceRepository();
        Instrument instrument1 = new Instrument("I001", "Instrument 1");
        Instrument instrument2 = new Instrument("I002", "Instrument 2");
        instrumentRepository.save(instrument1);
        instrumentRepository.save(instrument2);
        LocalDateTime priceDate = LocalDateTime.now();
        priceRepository.save(new Price("V001", "I001", new BigDecimal("10.25"), priceDate));
        priceRepository.save(new Price("V001", "I002", new BigDecimal("20.50"), priceDate));
        priceRepository.save(new Price("V002", "I001", new BigDecimal("9.75"), priceDate));

        GetPricesForAllInstrumentsOfGivenVendorService service = new GetPricesForAllInstrumentsOfGivenVendorService(priceRepository, instrumentRepository);

        Map<Instrument, Price> prices = service.getPrices("V001");

        assertEquals(Map.of(
                instrument1, new Price("V001", "I001", new BigDecimal("10.25"), priceDate),
                instrument2, new Price("V001", "I002", new BigDecimal("20.50"), priceDate)), prices);
    }

    @Test
    void returnsEmptyMapWhenVendorHasNoPrices() {
        GetPricesForAllInstrumentsOfGivenVendorService service =
                new GetPricesForAllInstrumentsOfGivenVendorService(new InMemoryPriceRepository(), new InMemoryInstrumentRepository());

        assertEquals(Map.of(), service.getPrices("unknown"));
    }
}
