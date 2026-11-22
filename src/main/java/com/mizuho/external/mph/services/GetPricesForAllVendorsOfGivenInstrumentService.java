package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GetPricesForAllVendorsOfGivenInstrumentService {

    private final PriceRepository priceRepository;
    private final VendorRepository vendorRepository;

    public GetPricesForAllVendorsOfGivenInstrumentService(PriceRepository priceRepository, VendorRepository vendorRepository) {
        this.priceRepository = priceRepository;
        this.vendorRepository = vendorRepository;
    }

    public Map<Vendor, Price> getPrices(String instrumentId) {
        Map<Vendor, Price> result = new HashMap<>();
        priceRepository.findByInstrumentId(instrumentId).forEach(price ->
                vendorRepository.findById(price.vendorId())
                        .ifPresent(vendor -> result.put(vendor, price)));
        return Collections.unmodifiableMap(result);
    }

    public Optional<Price> getBestPrice(String instrumentId) {
        return priceRepository.findBestPriceByInstrumentId(instrumentId);
    }

}