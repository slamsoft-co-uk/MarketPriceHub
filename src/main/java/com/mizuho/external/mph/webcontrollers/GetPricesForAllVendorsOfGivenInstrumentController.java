package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.response.GetPricesForAllVendorsOfGivenInstrumentResponse;
import com.mizuho.external.mph.domain.model.Price;
import com.mizuho.external.mph.services.GetPricesForAllVendorsOfGivenInstrumentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
public class GetPricesForAllVendorsOfGivenInstrumentController {

    private final GetPricesForAllVendorsOfGivenInstrumentService getPricesForAllVendorsOfGivenInstrumentService;

    public GetPricesForAllVendorsOfGivenInstrumentController(GetPricesForAllVendorsOfGivenInstrumentService getPricesForAllVendorsOfGivenInstrumentService) {
        this.getPricesForAllVendorsOfGivenInstrumentService = getPricesForAllVendorsOfGivenInstrumentService;
    }

    @GetMapping("/getPricesForAllVendorsOfGivenInstrument")
    public List<GetPricesForAllVendorsOfGivenInstrumentResponse> getPricesForAllVendorsOfGivenInstrument(
            @RequestParam("instrumentId") String instrumentId) {
        if (instrumentId.isBlank() || instrumentId.length() > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "instrumentId must be non-blank and at most 20 characters.");
        }

        return getPricesForAllVendorsOfGivenInstrumentService.getPrices(instrumentId).entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().id()))
                .map(entry -> new GetPricesForAllVendorsOfGivenInstrumentResponse(
                        entry.getKey().id(),
                        entry.getKey().name(),
                        entry.getValue().price(),
                        entry.getValue().priceDate()))
                .toList();
    }

    @GetMapping("/getBestPriceForAllVendorsOfGivenInstrument")
    public Price getBestPriceForAllVendorsOfGivenInstrument(@RequestParam("instrumentId") String instrumentId) {
        if (instrumentId.isBlank() || instrumentId.length() > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "instrumentId must be non-blank and at most 20 characters.");
        }

        return getPricesForAllVendorsOfGivenInstrumentService.getBestPrice(instrumentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No prices found for the given instrumentId."));
    }

}
