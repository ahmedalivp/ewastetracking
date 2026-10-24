package com.ewaste.tracking.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateFacilityRequest {

    @NotBlank(message = "Facility name is required")
    private String name;

    @NotBlank(message = "Facility address is required")
    private String address;

    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lng;

    private boolean verified;
    private boolean active = true;
    private String contactPhone;

    public UpdateFacilityRequest() {}

    public UpdateFacilityRequest(String name, String address, Double lat, Double lng, boolean verified, boolean active, String contactPhone) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.verified = verified;
        this.active = active;
        this.contactPhone = contactPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
