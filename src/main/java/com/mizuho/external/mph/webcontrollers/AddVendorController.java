package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.dto.request.VendorUpdateRequest;
import com.mizuho.external.mph.services.AddVendorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Locale;

@RestController
public class AddVendorController {

    private static final Logger logger = LoggerFactory.getLogger(AddVendorController.class);

    private final AddVendorService addVendorService;

    public AddVendorController(AddVendorService addVendorService) {
        this.addVendorService = addVendorService;
    }

    @PostMapping("/addVendor")
    public ResponseEntity<String> addVendor(
            @Valid @RequestBody VendorUpdateRequest vendorUpdateRequest, Principal principal) {
        long startTime = System.nanoTime();
        String userName = principal != null ? principal.getName() : "unknown";
        logger.info("Received request to add vendor: user={}, vendorId={}, vendorDescription={}",
                userName, vendorUpdateRequest.vendorId(), vendorUpdateRequest.vendorDescription());

        boolean completed = false;
        try {
            addVendorService.addVendor(new Vendor(
                    vendorUpdateRequest.vendorId(), vendorUpdateRequest.vendorDescription()));
            completed = true;
            return new ResponseEntity<>(
                    String.format("Insertion of vendor %s complete", vendorUpdateRequest.vendorId()), HttpStatus.OK);
        } finally {
            String durationSeconds = String.format(Locale.ROOT, "%.4f",
                    (System.nanoTime() - startTime) / 1_000_000_000.0);
            logger.info("Add vendor request finished: user={}, vendorId={}, vendorDescription={}, "
                            + "outcome={}, durationSeconds={}",
                    userName, vendorUpdateRequest.vendorId(), vendorUpdateRequest.vendorDescription(),
                    completed ? "success" : "failure", durationSeconds);
        }
    }

}
