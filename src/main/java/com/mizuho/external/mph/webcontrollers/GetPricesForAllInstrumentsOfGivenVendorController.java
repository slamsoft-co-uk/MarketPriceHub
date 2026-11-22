package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.response.GetPricesForAllInstrumentsOfGivenVendorResponse;
import com.mizuho.external.mph.services.GetPricesForAllInstrumentsOfGivenVendorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
public class GetPricesForAllInstrumentsOfGivenVendorController {

    private final GetPricesForAllInstrumentsOfGivenVendorService getPricesForAllInstrumentsOfGivenVendorService;

    public GetPricesForAllInstrumentsOfGivenVendorController(GetPricesForAllInstrumentsOfGivenVendorService getPricesForAllInstrumentsOfGivenVendorService) {
        this.getPricesForAllInstrumentsOfGivenVendorService = getPricesForAllInstrumentsOfGivenVendorService;
    }

    @GetMapping("/getPricesForAllInstrumentsOfGivenVendor")
    public List<GetPricesForAllInstrumentsOfGivenVendorResponse> getPricesForAllInstrumentsOfGivenVendor(@RequestParam("vendorId") String vendorId) {
        if (vendorId.isBlank() || vendorId.length() > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "vendorId must be non-blank and at most 20 characters.");
        }

        return getPricesForAllInstrumentsOfGivenVendorService.getPrices(vendorId).entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().id()))
                .map(entry -> new GetPricesForAllInstrumentsOfGivenVendorResponse(
                        entry.getKey().id(),
                        entry.getKey().name(),
                        entry.getValue().price(),
                        entry.getValue().priceDate()))
                .toList();
    }
}