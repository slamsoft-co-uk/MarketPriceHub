package com.mizuho.external.mph.dto.request;

import com.mizuho.external.mph.domain.dto.request.InstrumentUpdateRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstrumentUpdateRequestTest {

    @Test
    public void testObjectPopulatesCorrectly() {
        String instrumentId = "In001";
        String instrumentDescription = "Description for instrument In001";
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest(instrumentId, instrumentDescription);
        assertEquals(instrumentId, instrumentUpdateRequest.getInstrumentId());
        assertEquals(instrumentDescription, instrumentUpdateRequest.getInstrumentDescription());
    }

}
