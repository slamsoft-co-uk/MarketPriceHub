package com.mizuho.external.mph.webcontrollers;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.dto.request.VendorUpdateRequest;
import com.mizuho.external.mph.services.AddVendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddVendorController {

    private final AddVendorService addVendorService;

    public AddVendorController(AddVendorService addVendorService) {
        this.addVendorService = addVendorService;
    }

    @PostMapping("/addVendor")
    public ResponseEntity<String> addVendor(@Valid @RequestBody VendorUpdateRequest vendorUpdateRequest) {
        addVendorService.addVendor(new Vendor(
                vendorUpdateRequest.vendorId(), vendorUpdateRequest.vendorDescription()));
        return new ResponseEntity<>(
                String.format("Insertion of vendor %s complete", vendorUpdateRequest.vendorId()), HttpStatus.OK);
    }

}
