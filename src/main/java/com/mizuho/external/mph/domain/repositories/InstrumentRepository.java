package com.mizuho.external.mph.domain.repositories;

import com.mizuho.external.mph.domain.model.Instrument;

import java.util.Optional;

public interface InstrumentRepository {

    void save(Instrument instrument);

    boolean existsById(String instrumentId);

    Optional<Instrument> findById(String instrumentId);
}
