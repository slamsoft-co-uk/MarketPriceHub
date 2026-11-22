package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import com.mizuho.external.mph.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class PublishPricesService {

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
    }

}