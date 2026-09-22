package com.ewaste.app.data.local.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_drop_off_points")
public class CachedDropOffPoint {

    @PrimaryKey
    private Long id;
    private String label;
    private Double lat;
    private Double lng;
    private Long facilityId;
    private String facilityName;

    public CachedDropOffPoint() {}

    @androidx.room.Ignore
    public CachedDropOffPoint(Long id, String label, Double lat, Double lng, Long facilityId, String facilityName) {
        this.id = id;
        this.label = label;
        this.lat = lat;
        this.lng = lng;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
}
