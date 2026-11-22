package com.mizuho.external.mph.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VendorUpdateRequest(
        @NotBlank @Size(max = 20) String vendorId,
        @NotBlank @Size(max = 100) String vendorDescription
) {
    public String getVendorId() {
        return vendorId;
    }
    public String getVendorDescription() {
        return vendorDescription;
    }
}
