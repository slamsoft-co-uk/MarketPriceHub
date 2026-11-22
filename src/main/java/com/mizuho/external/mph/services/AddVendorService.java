package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.springframework.stereotype.Service;

@Service
public class AddVendorService {

    private final VendorRepository vendorRepository;

    public AddVendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public Vendor addVendor(Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("vendor must not be null.");
        }
        vendorRepository.save(vendor);
        return vendor;
    }
}