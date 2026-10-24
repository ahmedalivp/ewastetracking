package com.ewaste.tracking.dto.admin;

public class FacilityResponse {

    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private boolean verifiedByAdmin;
    private boolean active = true;
    private String contactPhone;
    private int staffCount;
    private int dropOffCount;

    public FacilityResponse() {}

    public FacilityResponse(Long id, String name, String address, Double lat, Double lng, boolean verifiedByAdmin) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.verifiedByAdmin = verifiedByAdmin;
        this.active = true;
    }

    public FacilityResponse(Long id, String name, String address, Double lat, Double lng, boolean verifiedByAdmin,
                            boolean active, String contactPhone, int staffCount, int dropOffCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.verifiedByAdmin = verifiedByAdmin;
        this.active = active;
        this.contactPhone = contactPhone;
        this.staffCount = staffCount;
        this.dropOffCount = dropOffCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isVerifiedByAdmin() {
        return verifiedByAdmin;
    }

    public void setVerifiedByAdmin(boolean verifiedByAdmin) {
        this.verifiedByAdmin = verifiedByAdmin;
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

    public int getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(int staffCount) {
        this.staffCount = staffCount;
    }

    public int getDropOffCount() {
        return dropOffCount;
    }

    public void setDropOffCount(int dropOffCount) {
        this.dropOffCount = dropOffCount;
    }
}
