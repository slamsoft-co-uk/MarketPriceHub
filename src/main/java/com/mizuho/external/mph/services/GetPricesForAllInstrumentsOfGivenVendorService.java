package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Instrument;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GetPricesForAllInstrumentsOfGivenVendorService {

    private static final Logger logger =
            LoggerFactory.getLogger(GetPricesForAllInstrumentsOfGivenVendorService.class);

    private final PriceRepository priceRepository;
    private final InstrumentRepository instrumentRepository;

    public GetPricesForAllInstrumentsOfGivenVendorService(PriceRepository priceRepository, InstrumentRepository instrumentRepository) {
        this.priceRepository = priceRepository;
        this.instrumentRepository = instrumentRepository;
    }

    public Map<Instrument, Price> getPrices(String vendorId) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            Map<Instrument, Price> result = new HashMap<>();
            priceRepository.findByVendorId(vendorId).forEach(price ->
                    instrumentRepository.findById(price.instrumentId())
                            .ifPresent(instrument -> result.put(instrument, price)));
            Map<Instrument, Price> prices = Collections.unmodifiableMap(result);
            completed = true;
            return prices;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "getPricesByVendor", startTime, completed);
        }
    }
}