package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.response.GetAllPricesResponse;
import com.mizuho.external.mph.services.GetAllPricesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@RestController
public class GetAllPricesController {

    private static final Logger logger = LoggerFactory.getLogger(GetAllPricesController.class);

    private final GetAllPricesService getAllPricesService;

    public GetAllPricesController(GetAllPricesService getAllPricesService) {
        this.getAllPricesService = getAllPricesService;
    }

    @GetMapping("/getAllPrices")
    public List<GetAllPricesResponse> getAllPrices(Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to get all prices: user={}", userName);

        boolean completed = false;
        try {
            List<GetAllPricesResponse> prices = getAllPricesService.getAllPrices().stream()
                    .sorted(Comparator.comparing(GetAllPricesResponse::vendorId)
                            .thenComparing(GetAllPricesResponse::instrumentId))
                    .toList();
            completed = true;
            return prices;
        } finally {
            String durationSeconds = String.format(Locale.ROOT, "%.4f",
                    (System.nanoTime() - startTime) / 1_000_000_000.0);
            logger.info("Get all prices request finished: user={}, outcome={}, durationSeconds={}",
                    userName, completed ? "success" : "failure", durationSeconds);
        }
    }
}
