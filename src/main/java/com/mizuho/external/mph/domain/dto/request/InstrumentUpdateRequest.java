package com.mizuho.external.mph.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InstrumentUpdateRequest(
        @NotBlank @Size(max = 20) String instrumentId,
        @NotBlank @Size(max = 100) String instrumentDescription
) {
    public String getInstrumentId() {
        return instrumentId;
    }

    public String getInstrumentDescription() {
        return instrumentDescription;
    }
}
