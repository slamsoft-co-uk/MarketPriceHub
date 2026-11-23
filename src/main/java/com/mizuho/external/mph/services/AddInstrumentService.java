package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AddInstrumentService {

    private static final Logger logger = LoggerFactory.getLogger(AddInstrumentService.class);

    private final InstrumentRepository instrumentRepository;

    public AddInstrumentService(InstrumentRepository instrumentRepository) {
        this.instrumentRepository = instrumentRepository;
    }

    public Instrument addInstrument(Instrument instrument) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            if (instrument == null) {
                throw new IllegalArgumentException("instrument must not be null.");
            }
            instrumentRepository.save(instrument);
            completed = true;
            return instrument;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "addInstrument", startTime, completed);
        }
    }
}