package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.dto.request.PublishPricesRequest;
import com.mizuho.external.mph.services.PublishPricesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublishPricesController {

    private final PublishPricesService publishPricesService;

    public PublishPricesController(PublishPricesService publishPricesService) {
        this.publishPricesService = publishPricesService;
    }

    @PostMapping("/publishPrices")
    public ResponseEntity<String> publishPrices(@Valid @RequestBody PublishPricesRequest request) {
        publishPricesService.publishPrice(
                request.vendorId(), request.instrumentId(), request.price(), request.priceDate());
        return new ResponseEntity<>(
                String.format("Insertion of a new price for instrument %s, vendor %s complete",
                        request.instrumentId(), request.vendorId()),
                HttpStatus.OK);
    }

}
