package com.ewaste.tracking.dto.admin;

import jakarta.validation.constraints.NotNull;

public class VerifyFacilityRequest {

    @NotNull(message = "Verification status flag is required")
    private Boolean verified;

    public VerifyFacilityRequest() {}

    public VerifyFacilityRequest(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
