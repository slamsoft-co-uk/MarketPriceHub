package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import org.springframework.stereotype.Service;

@Service
public class AddInstrumentService {

    private final InstrumentRepository instrumentRepository;

    public AddInstrumentService(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public Instrument addInstrument(Instrument instrument) {
        if (instrument == null) {
            throw new IllegalArgumentException("instrument must not be null.");
        }
        instrumentRepository.save(instrument);
        return instrument;
    }
}