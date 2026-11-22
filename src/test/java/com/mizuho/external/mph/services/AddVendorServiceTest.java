package com.mizuho.external.mph.services;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryVendorRepository;
import com.mizuho.external.mph.domain.repositories.VendorRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddVendorServiceTest {

    @Test
    void addsVendorToRepository() {
        VendorRepository repository = new InMemoryVendorRepository();
        AddVendorService service = new AddVendorService(repository);
        Vendor vendor = new Vendor("V567", "V567 Description");

        assertEquals(vendor, service.addVendor(vendor));
        assertEquals(vendor, repository.findById(vendor.id()).orElseThrow());
    }

    @Test
    void rejectsNullVendor() {
        AddVendorService service = new AddVendorService(new InMemoryVendorRepository());

        assertThrows(IllegalArgumentException.class, () -> service.addVendor(null));
    }
}
