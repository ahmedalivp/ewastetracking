package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("dashboardSummary")
    private String dashboardSummary;

    @SerializedName("creditBalance")
    private Integer creditBalance;

    @SerializedName("facilityId")
    private Long facilityId;

    @SerializedName("facilityName")
    private String facilityName;

    @SerializedName("businessName")
    private String businessName;

    @SerializedName("businessType")
    private String businessType;

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDashboardSummary() { return dashboardSummary; }
    public Integer getCreditBalance() { return creditBalance != null ? creditBalance : 0; }
    public Long getFacilityId() { return facilityId; }
    public String getFacilityName() { return facilityName; }
    public String getBusinessName() { return businessName; }
    public String getBusinessType() { return businessType; }
}
