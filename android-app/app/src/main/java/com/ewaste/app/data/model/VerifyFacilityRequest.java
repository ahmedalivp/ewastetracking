package com.ewaste.app.data.model;

public class VerifyFacilityRequest {

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
