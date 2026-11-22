package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class DeleteStalePricesService {

    private final PriceRepository priceRepository;

    public DeleteStalePricesService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public int deleteStalePrices(LocalDateTime staleDate) {
        Objects.requireNonNull(staleDate, "staleDate must not be null.");
        return priceRepository.deleteBefore(staleDate);
    }
}
