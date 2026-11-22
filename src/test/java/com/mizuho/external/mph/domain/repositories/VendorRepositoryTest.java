package com.mizuho.external.mph.domain.repositories;

import com.mizuho.external.mph.domain.model.Vendor;
import com.mizuho.external.mph.domain.repositories.impl.InMemoryVendorRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VendorRepositoryTest {

    @Test
    void savesVendorAndFindsItById() {
        VendorRepository repository = new InMemoryVendorRepository();
        Vendor vendor = new Vendor("V001", "Vendor 1");

        repository.save(vendor);

        assertEquals(vendor, repository.findById("V001").orElseThrow());
        assertTrue(repository.existsById("V001"));
    }

    @Test
    void savingVendorWithSameIdReplacesExistingVendor() {
        VendorRepository repository = new InMemoryVendorRepository();
        repository.save(new Vendor("V001", "Original"));
        Vendor replacement = new Vendor("V001", "Updated");

        repository.save(replacement);

        assertEquals(replacement, repository.findById("V001").orElseThrow());
    }

    @Test
    void returnsEmptyAndFalseForUnknownId() {
        VendorRepository repository = new InMemoryVendorRepository();

        assertTrue(repository.findById("missing").isEmpty());
        assertFalse(repository.existsById("missing"));
    }

    @Test
    void rejectsNullVendor() {
        VendorRepository repository = new InMemoryVendorRepository();

        assertThrows(NullPointerException.class, () -> repository.save(null));
    }
}
