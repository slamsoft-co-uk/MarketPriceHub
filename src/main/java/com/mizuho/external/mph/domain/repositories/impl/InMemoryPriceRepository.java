package com.mizuho.external.mph.domain.repositories.impl;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPriceRepository implements PriceRepository {

    private final Map<String, Map<String, Price>> pricesByInstrument = new ConcurrentHashMap<>();

    @Override
    public synchronized void save(Price price) {
        Objects.requireNonNull(price, "price cannot be null");
        pricesByInstrument
                .computeIfAbsent(price.instrumentId(), ignored -> new ConcurrentHashMap<>())
                .put(price.vendorId(), price);
    }

    @Override
    public synchronized void delete(String vendorId, String instrumentId) {
        pricesByInstrument.computeIfPresent(instrumentId, (ignored, prices) -> {
            prices.remove(vendorId);
            return prices.isEmpty() ? null : prices;
        });
    }

    @Override
    public synchronized int deleteBefore(LocalDateTime cutoff) {
        Objects.requireNonNull(cutoff, "cutoff cannot be null");
        List<Price> stalePrices = pricesByInstrument.values().stream()
                .flatMap(prices -> prices.values().stream())
                .filter(price -> price.priceDate().isBefore(cutoff))
                .toList();
        stalePrices.forEach(price -> delete(price.vendorId(), price.instrumentId()));
        return stalePrices.size();
    }

    @Override
    public synchronized Optional<Price> findByVendorAndInstrument(String vendorId, String instrumentId) {
        return Optional.ofNullable(pricesByInstrument.get(instrumentId))
                .map(prices -> prices.get(vendorId));
    }

    @Override
    public synchronized List<Price> findByVendorId(String vendorId) {
        return pricesByInstrument.values().stream()
                .map(prices -> prices.get(vendorId))
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public synchronized List<Price> findByInstrumentId(String instrumentId) {
        return List.copyOf(pricesByInstrument.getOrDefault(instrumentId, Map.of()).values());
    }

    @Override
    public synchronized Optional<Price> findBestPriceByInstrumentId(String instrumentId) {
        return findByInstrumentId(instrumentId).stream()
                .min(Comparator.comparing(Price::price));
    }
}
