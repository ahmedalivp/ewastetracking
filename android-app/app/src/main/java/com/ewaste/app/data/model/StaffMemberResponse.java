package com.ewaste.app.data.model;

public class StaffMemberResponse {
    private Long id;
    private String fullName;
    private String email;
    private Long facilityId;
    private String facilityName;
    private boolean active;
    private String createdAt;

    public StaffMemberResponse() {}

    public StaffMemberResponse(Long id, String fullName, String email, Long facilityId,
                               String facilityName, boolean active, String createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }
    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
