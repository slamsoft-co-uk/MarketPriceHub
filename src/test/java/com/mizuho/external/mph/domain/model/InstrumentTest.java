package com.mizuho.external.mph.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InstrumentTest {

    @Test
    void storesIdAndName() {
        Instrument instrument = new Instrument("I001", "Instrument 1");

        assertEquals("I001", instrument.id());
        assertEquals("Instrument 1", instrument.name());
    }

    @Test
    void hasValueBasedEqualityAndHashCode() {
        Instrument instrument = new Instrument("I001", "Instrument 1");

        assertEquals(new Instrument("I001", "Instrument 1"), instrument);
        assertEquals(new Instrument("I001", "Instrument 1").hashCode(), instrument.hashCode());
        assertNotEquals(new Instrument("I002", "Instrument 1"), instrument);
        assertNotEquals(new Instrument("I001", "Another instrument"), instrument);
    }
}
