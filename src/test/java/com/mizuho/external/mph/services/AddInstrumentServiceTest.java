package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryInstrumentRepository;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddInstrumentServiceTest {

    @Test
    void addsInstrumentToRepository() {
        InstrumentRepository repository = new InMemoryInstrumentRepository();
        AddInstrumentService service = new AddInstrumentService(repository);
        Instrument instrument = new Instrument("I567", "I567 Description");

        assertEquals(instrument, service.addInstrument(instrument));
        assertEquals(instrument, repository.findById(instrument.id()).orElseThrow());
    }

    @Test
    void rejectsNullInstrument() {
        AddInstrumentService service = new AddInstrumentService(new InMemoryInstrumentRepository());

        assertThrows(IllegalArgumentException.class, () -> service.addInstrument(null));
    }
}
