package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import com.mizuho.external.mph.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class PublishPricesService {

    private static final Logger logger = LoggerFactory.getLogger(PublishPricesService.class);

    private final VendorRepository vendorRepository;
    private final InstrumentRepository instrumentRepository;
    private final PriceRepository priceRepository;

    public PublishPricesService(
            VendorRepository vendorRepository,
            InstrumentRepository instrumentRepository,
            PriceRepository priceRepository) {
        this.vendorRepository = vendorRepository;
        this.instrumentRepository = instrumentRepository;
        this.priceRepository = priceRepository;
    }

    public void publishPrice(String vendorId, String instrumentId, BigDecimal price, LocalDateTime priceDate) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            Objects.requireNonNull(vendorId, "vendorId must not be null.");
            Objects.requireNonNull(instrumentId, "instrumentId must not be null.");
            Objects.requireNonNull(price, "price must not be null.");
            Objects.requireNonNull(priceDate, "priceDate must not be null.");

            if (!instrumentRepository.existsById(instrumentId)) {
                throw new ResourceNotFoundException("Instrument " + instrumentId + " was not found.");
            }
            if (!vendorRepository.existsById(vendorId)) {
                throw new ResourceNotFoundException("Vendor " + vendorId + " was not found.");
            }
            priceRepository.save(new Price(vendorId, instrumentId, price, priceDate));
            logger.info("Price cache size after publishing: {} prices", priceRepository.countAll());
            completed = true;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "publishPrice", startTime, completed);
        }
    }

}