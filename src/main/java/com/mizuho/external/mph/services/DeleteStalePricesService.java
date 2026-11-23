package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class DeleteStalePricesService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteStalePricesService.class);

    private final PriceRepository priceRepository;

    public DeleteStalePricesService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public int deleteStalePrices(LocalDateTime staleDate) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            Objects.requireNonNull(staleDate, "staleDate must not be null.");
            int deleted = priceRepository.deleteBefore(staleDate);
            completed = true;
            return deleted;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "deleteStalePrices", startTime, completed);
        }
    }
}
