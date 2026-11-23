package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.response.GetPricesForAllVendorsOfGivenInstrumentResponse;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.services.GetPricesForAllVendorsOfGivenInstrumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestController
public class GetPricesForAllVendorsOfGivenInstrumentController {

    private static final Logger logger =
            LoggerFactory.getLogger(GetPricesForAllVendorsOfGivenInstrumentController.class);

    private final GetPricesForAllVendorsOfGivenInstrumentService getPricesForAllVendorsOfGivenInstrumentService;

    public GetPricesForAllVendorsOfGivenInstrumentController(GetPricesForAllVendorsOfGivenInstrumentService getPricesForAllVendorsOfGivenInstrumentService) {
        this.getPricesForAllVendorsOfGivenInstrumentService = getPricesForAllVendorsOfGivenInstrumentService;
    }

    @GetMapping("/getPricesForAllVendorsOfGivenInstrument")
    public List<GetPricesForAllVendorsOfGivenInstrumentResponse> getPricesForAllVendorsOfGivenInstrument(
            @RequestParam("instrumentId") String instrumentId, Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to get prices for vendors of instrument: user={}, instrumentId={}",
                userName, instrumentId);

        boolean completed = false;
        try {
            if (instrumentId.isBlank() || instrumentId.length() > 20) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "instrumentId must be non-blank and at most 20 characters.");
            }

            List<GetPricesForAllVendorsOfGivenInstrumentResponse> prices =
                    getPricesForAllVendorsOfGivenInstrumentService.getPrices(instrumentId).entrySet().stream()
                            .sorted(Comparator.comparing(entry -> entry.getKey().id()))
                            .map(entry -> new GetPricesForAllVendorsOfGivenInstrumentResponse(
                                    entry.getKey().id(),
                                    entry.getKey().name(),
                                    entry.getValue().price(),
                                    entry.getValue().priceDate()))
                            .toList();
            completed = true;
            return prices;
        } finally {
            String durationSeconds = String.format(Locale.ROOT, "%.4f",
                    (System.nanoTime() - startTime) / 1_000_000_000.0);
            logger.info("Get prices for vendors of instrument request finished: user={}, instrumentId={}, "
                            + "outcome={}, durationSeconds={}",
                    userName, instrumentId, completed ? "success" : "failure", durationSeconds);
        }
    }

    @GetMapping("/getBestPriceForAllVendorsOfGivenInstrument")
    public Price getBestPriceForAllVendorsOfGivenInstrument(
            @RequestParam("instrumentId") String instrumentId, Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to get best price for instrument: user={}, instrumentId={}",
                userName, instrumentId);

        boolean completed = false;
        try {
            if (instrumentId.isBlank() || instrumentId.length() > 20) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "instrumentId must be non-blank and at most 20 characters.");
            }

            Price price = getPricesForAllVendorsOfGivenInstrumentService.getBestPrice(instrumentId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "No prices found for the given instrumentId."));
            completed = true;
            return price;
        } finally {
            String durationSeconds = String.format(Locale.ROOT, "%.4f",
                    (System.nanoTime() - startTime) / 1_000_000_000.0);
            logger.info("Get best price for instrument request finished: user={}, instrumentId={}, "
                            + "outcome={}, durationSeconds={}",
                    userName, instrumentId, completed ? "success" : "failure", durationSeconds);
        }
    }

}
