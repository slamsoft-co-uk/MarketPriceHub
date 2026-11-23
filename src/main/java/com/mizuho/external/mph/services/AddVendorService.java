package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AddVendorService {

    private static final Logger logger = LoggerFactory.getLogger(AddVendorService.class);

    private final VendorRepository vendorRepository;

    public AddVendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public Vendor addVendor(Vendor vendor) {
        long startTime = System.nanoTime();
        boolean completed = false;
        try {
            if (vendor == null) {
                throw new IllegalArgumentException("vendor must not be null.");
            }
            vendorRepository.save(vendor);
            completed = true;
            return vendor;
        } finally {
            ServiceExecutionLogger.logCompletion(logger, "addVendor", startTime, completed);
        }
    }
}