package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GetPricesForAllVendorsOfGivenInstrumentService {

    private static final Logger logger =
            LoggerFactory.getLogger(GetPricesForAllVendorsOfGivenInstrumentService.class);

    private final PriceRepository priceRepository;
    private final VendorRepository vendorRepository;

    public GetPricesForAllVendorsOfGivenInstrumentService(PriceRepository priceRepository, VendorRepository vendorRepository) {
        this.priceRepository = priceRepository;
        this.vendorRepository = vendorRepository;
    }

    public Map<Vendor, Price> getPrices(String instrumentId) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            Map<Vendor, Price> result = new HashMap<>();
            priceRepository.findByInstrumentId(instrumentId).forEach(price ->
                    vendorRepository.findById(price.vendorId())
                            .ifPresent(vendor -> result.put(vendor, price)));
            Map<Vendor, Price> prices = Collections.unmodifiableMap(result);
            completed = true;
            return prices;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "getPricesByInstrument", startTime, completed);
        }
    }

    public Optional<Price> getBestPrice(String instrumentId) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            Optional<Price> price = priceRepository.findBestPriceByInstrumentId(instrumentId);
            completed = true;
            return price;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "getBestPrice", startTime, completed);
        }
    }

}