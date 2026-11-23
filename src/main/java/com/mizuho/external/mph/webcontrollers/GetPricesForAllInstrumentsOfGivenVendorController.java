package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.response.GetPricesForAllInstrumentsOfGivenVendorResponse;
import com.mizuho.external.mph.services.GetPricesForAllInstrumentsOfGivenVendorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestController
public class GetPricesForAllInstrumentsOfGivenVendorController {

    private static final Logger logger =
            LoggerFactory.getLogger(GetPricesForAllInstrumentsOfGivenVendorController.class);

    private final GetPricesForAllInstrumentsOfGivenVendorService getPricesForAllInstrumentsOfGivenVendorService;

    public GetPricesForAllInstrumentsOfGivenVendorController(GetPricesForAllInstrumentsOfGivenVendorService getPricesForAllInstrumentsOfGivenVendorService) {
        this.getPricesForAllInstrumentsOfGivenVendorService = getPricesForAllInstrumentsOfGivenVendorService;
    }

    @GetMapping("/getPricesForAllInstrumentsOfGivenVendor")
    public List<GetPricesForAllInstrumentsOfGivenVendorResponse> getPricesForAllInstrumentsOfGivenVendor(
            @RequestParam("vendorId") String vendorId, Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to get prices for instruments of vendor: user={}, vendorId={}",
                userName, vendorId);

        boolean completed = false;
        try {
            if (vendorId.isBlank() || vendorId.length() > 20) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "vendorId must be non-blank and at most 20 characters.");
            }

            List<GetPricesForAllInstrumentsOfGivenVendorResponse> prices =
                    getPricesForAllInstrumentsOfGivenVendorService.getPrices(vendorId).entrySet().stream()
                            .sorted(Comparator.comparing(entry -> entry.getKey().id()))
                            .map(entry -> new GetPricesForAllInstrumentsOfGivenVendorResponse(
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
            logger.info("Get prices for instruments of vendor request finished: user={}, vendorId={}, "
                            + "outcome={}, durationSeconds={}",
                    userName, vendorId, completed ? "success" : "failure", durationSeconds);
        }
    }
}