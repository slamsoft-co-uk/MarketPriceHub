package com.mizuho.external.mph.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VendorTest {

    @Test
    void storesIdAndName() {
        Vendor vendor = new Vendor("V001", "Vendor 1");

        assertEquals("V001", vendor.id());
        assertEquals("Vendor 1", vendor.name());
    }

    @Test
    void hasValueBasedEqualityAndHashCode() {
        Vendor vendor = new Vendor("V001", "Vendor 1");

        assertEquals(new Vendor("V001", "Vendor 1"), vendor);
        assertEquals(new Vendor("V001", "Vendor 1").hashCode(), vendor.hashCode());
        assertNotEquals(new Vendor("V002", "Vendor 1"), vendor);
        assertNotEquals(new Vendor("V001", "Another vendor"), vendor);
    }
}
