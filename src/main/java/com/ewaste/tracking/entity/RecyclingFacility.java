package com.ewaste.tracking.entity;

import jakarta.persistence.*;

/**
 * OOP PRINCIPLE: ENCAPSULATION
 * 
 * Represents a certified or registered recycling facility.
 * Fields are private with strict encapsulation via accessors/mutators.
 */
@Entity
@Table(name = "recycling_facilities")
public class RecyclingFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(nullable = false)
    private boolean verifiedByAdmin;

    @Column(nullable = false)
    private boolean active = true;

    @Column
    private String contactPhone;

    public RecyclingFacility() {
        this.verifiedByAdmin = false;
        this.active = true;
    }

    public RecyclingFacility(String name, String address, Double lat, Double lng, boolean verifiedByAdmin) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.verifiedByAdmin = verifiedByAdmin;
        this.active = true;
    }

    public RecyclingFacility(String name, String address, Double lat, Double lng, boolean verifiedByAdmin, String contactPhone) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.verifiedByAdmin = verifiedByAdmin;
        this.active = true;
        this.contactPhone = contactPhone;
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
}
