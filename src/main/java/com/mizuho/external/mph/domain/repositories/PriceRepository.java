package com.mizuho.external.mph.domain.repositories;

import com.mizuho.external.mph.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PriceRepository {

    void save(Price price);

    void delete(String vendorId, String instrumentId);

    int deleteBefore(LocalDateTime cutoff);

    Optional<Price> findByVendorAndInstrument(String vendorId, String instrumentId);

    List<Price> findByVendorId(String vendorId);

    List<Price> findByInstrumentId(String instrumentId);

    List<Price> findAll();

    long countAll();

    Optional<Price> findBestPriceByInstrumentId(String instrumentId);
}
