package com.ewaste.tracking.entity;

import jakarta.persistence.*;

/**
 * OOP PRINCIPLE: ENCAPSULATION & COMPOSITION
 * 
 * Drop-off collection station associated with a parent recycling facility.
 */
@Entity
@Table(name = "drop_off_points")
public class DropOffPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "facility_id", nullable = false)
    private RecyclingFacility facility;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    public DropOffPoint() {}

    public DropOffPoint(RecyclingFacility facility, String label, Double lat, Double lng) {
        this.facility = facility;
        this.label = label;
        this.lat = lat;
        this.lng = lng;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecyclingFacility getFacility() {
        return facility;
    }

    public void setFacility(RecyclingFacility facility) {
        this.facility = facility;
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
}
