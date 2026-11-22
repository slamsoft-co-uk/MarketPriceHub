package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryPriceRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteStalePricesServiceTest {

    @Test
    void removesPricesOlderThanCutoffFromBothIndexes() {
        PriceRepository repository = new InMemoryPriceRepository();
        LocalDateTime cutoff = LocalDateTime.of(2026, 11, 21, 12, 0);
        repository.save(new Price("V001", "I001", BigDecimal.ONE, cutoff.minusNanos(1)));
        repository.save(new Price("V001", "I002", new BigDecimal("2"), cutoff));

        DeleteStalePricesService service = new DeleteStalePricesService(repository);

        assertEquals(1, service.deleteStalePrices(cutoff));
        assertEquals(1, repository.findByVendorId("V001").size());
        assertEquals("I002", repository.findByVendorId("V001").getFirst().instrumentId());
        assertEquals("V001", repository.findByInstrumentId("I002").getFirst().vendorId());
        assertEquals(0, repository.findByInstrumentId("I001").size());
    }

    @Test
    void rejectsNullCutoff() {
        DeleteStalePricesService service = new DeleteStalePricesService(new InMemoryPriceRepository());

        assertThrows(NullPointerException.class, () -> service.deleteStalePrices(null));
    }
}
