package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    private String role; // CONSUMER, FACILITY_STAFF, BUSINESS

    @SerializedName("facilityId")
    private Long facilityId;

    @SerializedName("businessName")
    private String businessName;

    @SerializedName("businessType")
    private String businessType;

    public RegisterRequest(String fullName, String email, String password, String role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public Long getFacilityId() { return facilityId; }
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
}
