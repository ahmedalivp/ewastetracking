package com.ewaste.app.data.local.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_submissions")
public class CachedSubmission {

    @PrimaryKey
    private Long id;
    private String categoryName;
    private String deviceDescription;
    private String dropOffPointLabel;
    private String status;
    private String submittedAt;
    private int componentCount;
    private int hazardousMaterialCount;

    public CachedSubmission() {}

    @androidx.room.Ignore
    public CachedSubmission(Long id, String categoryName, String deviceDescription, String dropOffPointLabel,
                            String status, String submittedAt, int componentCount, int hazardousMaterialCount) {
        this.id = id;
        this.categoryName = categoryName;
        this.deviceDescription = deviceDescription;
        this.dropOffPointLabel = dropOffPointLabel;
        this.status = status;
        this.submittedAt = submittedAt;
        this.componentCount = componentCount;
        this.hazardousMaterialCount = hazardousMaterialCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public String getDropOffPointLabel() {
        return dropOffPointLabel;
    }

    public void setDropOffPointLabel(String dropOffPointLabel) {
        this.dropOffPointLabel = dropOffPointLabel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public void setComponentCount(int componentCount) {
        this.componentCount = componentCount;
    }

    public int getHazardousMaterialCount() {
        return hazardousMaterialCount;
    }

    public void setHazardousMaterialCount(int hazardousMaterialCount) {
        this.hazardousMaterialCount = hazardousMaterialCount;
    }
}
