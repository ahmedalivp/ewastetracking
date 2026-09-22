package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private Long id;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("role")
    private String role;

    @SerializedName("dashboardSummary")
    private String dashboardSummary;

    public String getToken() { return token; }
    public String getType() { return type; }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getDashboardSummary() { return dashboardSummary; }
}
