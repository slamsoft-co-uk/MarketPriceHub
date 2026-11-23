package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.dto.response.GetAllPricesResponse;
import com.mizuho.external.mph.domain.repositories.InstrumentRepository;
import com.mizuho.external.mph.domain.repositories.PriceRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllPricesService {

    private static final Logger logger = LoggerFactory.getLogger(GetAllPricesService.class);

    private final PriceRepository priceRepository;
    private final VendorRepository vendorRepository;
    private final InstrumentRepository instrumentRepository;

    public GetAllPricesService(
            PriceRepository priceRepository,
            VendorRepository vendorRepository,
            InstrumentRepository instrumentRepository) {
        this.priceRepository = priceRepository;
        this.vendorRepository = vendorRepository;
        this.instrumentRepository = instrumentRepository;
    }

    public List<GetAllPricesResponse> getAllPrices() {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            List<GetAllPricesResponse> prices = priceRepository.findAll().stream()
                    .map(price -> vendorRepository.findById(price.vendorId())
                            .flatMap(vendor -> instrumentRepository.findById(price.instrumentId())
                                    .map(instrument -> new GetAllPricesResponse(
                                            vendor.id(),
                                            vendor.name(),
                                            instrument.id(),
                                            instrument.name(),
                                            price.price(),
                                            price.priceDate()))))
                    .flatMap(java.util.Optional::stream)
                    .toList();
            completed = true;
            return prices;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "getAllPrices", startTime, completed);
        }
    }
}
