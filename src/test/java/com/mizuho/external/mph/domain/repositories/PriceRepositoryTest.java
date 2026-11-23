package com.mizuho.external.mph.domain.repositories;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceRepositoryTest {

    private final LocalDateTime priceDate = LocalDateTime.of(2026, 11, 21, 12, 0);

    @Test
    void savesAndFindsPriceByVendorAndInstrument() {
        PriceRepository repository = new InMemoryPriceRepository();
        Price first = price("V001", "I001", "10.25");
        Price second = price("V001", "I002", "20.50");
        Price third = price("V002", "I001", "9.75");

        repository.save(first);
        repository.save(second);
        repository.save(third);

        assertEquals(first, repository.findByVendorAndInstrument("V001", "I001").orElseThrow());
        assertEquals(List.of(first, second), repository.findByVendorId("V001").stream()
                .sorted(Comparator.comparing(Price::instrumentId)).toList());
        assertEquals(List.of(first, third), repository.findByInstrumentId("I001").stream()
                .sorted(Comparator.comparing(Price::vendorId)).toList());
        assertEquals(3, repository.countAll());
    }

    @Test
    void savingSameVendorInstrumentReplacesPriceInBothIndexes() {
        PriceRepository repository = new InMemoryPriceRepository();
        repository.save(price("V001", "I001", "10.25"));
        Price updated = price("V001", "I001", "12.50");

        repository.save(updated);

        assertEquals(updated, repository.findByVendorAndInstrument("V001", "I001").orElseThrow());
        assertEquals(List.of(updated), repository.findByVendorId("V001"));
        assertEquals(List.of(updated), repository.findByInstrumentId("I001"));
        assertEquals(1, repository.countAll());
    }

    @Test
    void rejectsNullPrice() {
        PriceRepository repository = new InMemoryPriceRepository();

        assertThrows(NullPointerException.class, () -> repository.save(null));
    }

    @Test
    void deleteRemovesPriceFromBothIndexesAndCleansEmptyEntries() {
        PriceRepository repository = new InMemoryPriceRepository();
        repository.save(price("V001", "I001", "10.25"));
        repository.save(price("V001", "I002", "20.50"));
        repository.save(price("V002", "I001", "9.75"));

        repository.delete("V001", "I001");

        assertFalse(repository.findByVendorAndInstrument("V001", "I001").isPresent());
        assertEquals(List.of(price("V001", "I002", "20.50")), repository.findByVendorId("V001"));
        assertEquals(List.of(price("V002", "I001", "9.75")), repository.findByInstrumentId("I001"));
        assertEquals(2, repository.countAll());

        repository.delete("V001", "I002");
        assertTrue(repository.findByVendorId("V001").isEmpty());
        assertTrue(repository.findByInstrumentId("I002").isEmpty());
        assertEquals(1, repository.countAll());
    }

    @Test
    void deletingUnknownPriceDoesNothing() {
        PriceRepository repository = new InMemoryPriceRepository();
        repository.save(price("V001", "I001", "10.25"));

        repository.delete("unknown-vendor", "unknown-instrument");
        repository.delete("V001", "unknown-instrument");

        assertEquals(List.of(price("V001", "I001", "10.25")), repository.findByVendorId("V001"));
    }

    @Test
    void deleteBeforeRemovesOnlyPricesStrictlyOlderThanCutoff() {
        PriceRepository repository = new InMemoryPriceRepository();
        repository.save(new Price("V001", "I001", BigDecimal.ONE, priceDate.minusNanos(1)));
        Price onCutoff = new Price("V001", "I002", new BigDecimal("2"), priceDate);
        repository.save(onCutoff);

        assertEquals(1, repository.deleteBefore(priceDate));
        assertEquals(List.of(onCutoff), repository.findByVendorId("V001"));
        assertTrue(repository.findByInstrumentId("I001").isEmpty());
        assertEquals(0, repository.deleteBefore(priceDate));
    }

    @Test
    void deleteBeforeRejectsNullCutoff() {
        PriceRepository repository = new InMemoryPriceRepository();

        assertThrows(NullPointerException.class, () -> repository.deleteBefore(null));
    }

    @Test
    void findsBestPriceAndReturnsEmptyForUnknownInstrument() {
        PriceRepository repository = new InMemoryPriceRepository();
        Price moreExpensive = price("V001", "I001", "10.25");
        Price cheapest = price("V002", "I001", "9.75");
        repository.save(moreExpensive);
        repository.save(cheapest);

        assertEquals(cheapest, repository.findBestPriceByInstrumentId("I001").orElseThrow());
        assertTrue(repository.findBestPriceByInstrumentId("unknown").isEmpty());
    }

    @Test
    void lookupsReturnEmptyCollectionsWhenNoPricesExist() {
        PriceRepository repository = new InMemoryPriceRepository();

        assertTrue(repository.findByVendorAndInstrument("V001", "I001").isEmpty());
        assertTrue(repository.findByVendorId("V001").isEmpty());
        assertTrue(repository.findByInstrumentId("I001").isEmpty());
    }

    @Test
    void vendorAndInstrumentLookupsReturnEmptyWhenOnlyOneIndexKeyExists() {
        PriceRepository repository = new InMemoryPriceRepository();
        repository.save(price("V001", "I001", "10.25"));

        assertTrue(repository.findByVendorAndInstrument("V001", "missing-instrument").isEmpty());
        assertTrue(repository.findByVendorAndInstrument("missing-vendor", "I001").isEmpty());
    }

    private Price price(String vendorId, String instrumentId, String amount) {
        return new Price(vendorId, instrumentId, new BigDecimal(amount), priceDate);
    }
}
