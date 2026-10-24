package com.ewaste.tracking.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateDropOffPointRequest {

    private Long facilityId;

    @NotBlank(message = "Label is required")
    private String label;

    @NotNull(message = "Latitude is required")
    private Double lat;

    @NotNull(message = "Longitude is required")
    private Double lng;

    private String address;
    private String imageUrl;
    private String operatingHours;
    private String contactPhone;
    private String acceptedTypes;

    public UpdateDropOffPointRequest() {}

    public UpdateDropOffPointRequest(Long facilityId, String label, Double lat, Double lng,
                                    String address, String imageUrl, String operatingHours,
                                    String contactPhone, String acceptedTypes) {
        this.facilityId = facilityId;
        this.label = label;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.imageUrl = imageUrl;
        this.operatingHours = operatingHours;
        this.contactPhone = contactPhone;
        this.acceptedTypes = acceptedTypes;
    }

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAcceptedTypes() {
        return acceptedTypes;
    }

    public void setAcceptedTypes(String acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }
}
