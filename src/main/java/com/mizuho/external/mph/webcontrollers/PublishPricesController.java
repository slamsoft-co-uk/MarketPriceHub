package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.request.PublishPricesRequest;
import com.mizuho.external.mph.services.PublishPricesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Locale;

@RestController
public class PublishPricesController {

    private static final Logger logger = LoggerFactory.getLogger(PublishPricesController.class);

    private final PublishPricesService publishPricesService;

    public PublishPricesController(PublishPricesService publishPricesService) {
        this.publishPricesService = publishPricesService;
    }

    @PostMapping("/publishPrices")
    public ResponseEntity<String> publishPrices(
            @Valid @RequestBody PublishPricesRequest request, Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to publish price: user={}, vendorId={}, instrumentId={}, price={}, priceDate={}",
                userName, request.vendorId(), request.instrumentId(), request.price(), request.priceDate());

        boolean completed = false;
        try {
            publishPricesService.publishPrice(
                    request.vendorId(), request.instrumentId(), request.price(), request.priceDate());
            completed = true;
            return new ResponseEntity<>(
                    String.format("Insertion of a new price for instrument %s, vendor %s complete",
                            request.instrumentId(), request.vendorId()),
                    HttpStatus.OK);
        } finally {
            String durationSeconds = String.format(Locale.ROOT, "%.4f",
                    (System.nanoTime() - startTime) / 1_000_000_000.0);
            logger.info("Publish price request finished: user={}, vendorId={}, instrumentId={}, price={}, "
                            + "priceDate={}, outcome={}, durationSeconds={}",
                    userName, request.vendorId(), request.instrumentId(), request.price(), request.priceDate(),
                    completed ? "success" : "failure", durationSeconds);
        }
    }

}
