package com.ewaste.tracking.dto.admin;

public class UpdateStaffAccessRequest {

    private Long facilityId;
    private Boolean active;

    public UpdateStaffAccessRequest() {}

    public UpdateStaffAccessRequest(Long facilityId, Boolean active) {
        this.facilityId = facilityId;
        this.active = active;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
