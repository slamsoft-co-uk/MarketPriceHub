package com.mizuho.external.mph.domain.repositories;

import com.mizuho.external.mph.domain.model.Vendor;

import java.util.Optional;

public interface VendorRepository {

    void save(Vendor vendor);

    boolean existsById(String vendorId);

    Optional<Vendor> findById(String vendorId);
}
