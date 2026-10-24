package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class DropOffPointResponse implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("label")
    private String label;

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lng")
    private Double lng;

    @SerializedName("facilityId")
    private Long facilityId;

    @SerializedName("facilityName")
    private String facilityName;

    @SerializedName("address")
    private String address;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("operatingHours")
    private String operatingHours;

    @SerializedName("contactPhone")
    private String contactPhone;

    @SerializedName("acceptedTypes")
    private String acceptedTypes;

    public DropOffPointResponse() {}

    public DropOffPointResponse(Long id, String label, Double lat, Double lng, Long facilityId, String facilityName) {
        this.id = id;
        this.label = label;
        this.lat = lat;
        this.lng = lng;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }

    public DropOffPointResponse(Long id, String label, Double lat, Double lng, Long facilityId, String facilityName,
                                String address, String imageUrl, String operatingHours, String contactPhone, String acceptedTypes) {
        this.id = id;
        this.label = label;
        this.lat = lat;
        this.lng = lng;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.address = address;
        this.imageUrl = imageUrl;
        this.operatingHours = operatingHours;
        this.contactPhone = contactPhone;
        this.acceptedTypes = acceptedTypes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getAcceptedTypes() { return acceptedTypes; }
    public void setAcceptedTypes(String acceptedTypes) { this.acceptedTypes = acceptedTypes; }

    @Override
    public String toString() {
        return label != null ? label : "";
    }
}
