package com.mizuho.external.mph.domain.repositories.impl;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryVendorRepository implements VendorRepository {

    private final Map<String, Vendor> vendors = new ConcurrentHashMap<>();

    @Override
    public void save(Vendor vendor) {
        Objects.requireNonNull(vendor, "vendor cannot be null");
        vendors.put(vendor.id(), vendor);
    }

    @Override
    public boolean existsById(String vendorId) {
        return vendors.containsKey(vendorId);
    }

    @Override
    public Optional<Vendor> findById(String vendorId) {
        return Optional.ofNullable(vendors.get(vendorId));
    }
}
