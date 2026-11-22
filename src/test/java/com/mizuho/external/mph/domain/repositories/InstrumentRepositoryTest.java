package com.mizuho.external.mph.domain.repositories;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryInstrumentRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstrumentRepositoryTest {

    @Test
    void savesInstrumentAndFindsItById() {
        InstrumentRepository repository = new InMemoryInstrumentRepository();
        Instrument instrument = new Instrument("I001", "Instrument 1");

        repository.save(instrument);

        assertEquals(instrument, repository.findById("I001").orElseThrow());
        assertTrue(repository.existsById("I001"));
    }

    @Test
    void savingInstrumentWithSameIdReplacesExistingInstrument() {
        InstrumentRepository repository = new InMemoryInstrumentRepository();
        repository.save(new Instrument("I001", "Original"));
        Instrument replacement = new Instrument("I001", "Updated");

        repository.save(replacement);

        assertEquals(replacement, repository.findById("I001").orElseThrow());
    }

    @Test
    void returnsEmptyAndFalseForUnknownId() {
        InstrumentRepository repository = new InMemoryInstrumentRepository();

        assertTrue(repository.findById("missing").isEmpty());
        assertFalse(repository.existsById("missing"));
    }

    @Test
    void rejectsNullInstrument() {
        InstrumentRepository repository = new InMemoryInstrumentRepository();

        assertThrows(NullPointerException.class, () -> repository.save(null));
    }
}
