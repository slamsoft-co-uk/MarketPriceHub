package com.mizuho.external.mph.domain.repositories.impl;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInstrumentRepository implements InstrumentRepository {

    private final Map<String, Instrument> instruments = new ConcurrentHashMap<>();

    @Override
    public void save(Instrument instrument) {
        Objects.requireNonNull(instrument, "instrument cannot be null");
        instruments.put(instrument.id(), instrument);
    }

    @Override
    public boolean existsById(String instrumentId) {
        return instruments.containsKey(instrumentId);
    }

    @Override
    public Optional<Instrument> findById(String instrumentId) {
        return Optional.ofNullable(instruments.get(instrumentId));
    }
}
