package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GetPricesForAllInstrumentsOfGivenVendorService {

    private final PriceRepository priceRepository;
    private final InstrumentRepository instrumentRepository;

    public GetPricesForAllInstrumentsOfGivenVendorService(PriceRepository priceRepository, InstrumentRepository instrumentRepository) {
        this.priceRepository = priceRepository;
        this.instrumentRepository = instrumentRepository;
    }

    public Map<Instrument, Price> getPrices(String vendorId) {
        Map<Instrument, Price> result = new HashMap<>();
        priceRepository.findByVendorId(vendorId).forEach(price ->
                instrumentRepository.findById(price.instrumentId())
                        .ifPresent(instrument -> result.put(instrument, price)));
        return Collections.unmodifiableMap(result);
    }
}