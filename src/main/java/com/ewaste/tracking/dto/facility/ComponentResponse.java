package com.ewaste.tracking.dto.facility;

import com.ewaste.tracking.enums.ComponentCondition;
import com.ewaste.tracking.enums.ComponentStatus;
import java.time.LocalDateTime;

public class ComponentResponse {

    private Long id;
    private Long ewasteItemId;
    private String categoryName;
    private String name;
    private String type;
    private ComponentCondition condition;
    private ComponentStatus status;
    private LocalDateTime harvestedAt;

    public ComponentResponse() {}

    public ComponentResponse(Long id, Long ewasteItemId, String categoryName, String name,
                             String type, ComponentCondition condition, ComponentStatus status,
                             LocalDateTime harvestedAt) {
        this.id = id;
        this.ewasteItemId = ewasteItemId;
        this.categoryName = categoryName;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.status = status;
        this.harvestedAt = harvestedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEwasteItemId() {
        return ewasteItemId;
    }

    public void setEwasteItemId(Long ewasteItemId) {
        this.ewasteItemId = ewasteItemId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ComponentCondition getCondition() {
        return condition;
    }

    public void setCondition(ComponentCondition condition) {
        this.condition = condition;
    }

    public ComponentStatus getStatus() {
        return status;
    }

    public void setStatus(ComponentStatus status) {
        this.status = status;
    }

    public LocalDateTime getHarvestedAt() {
        return harvestedAt;
    }

    public void setHarvestedAt(LocalDateTime harvestedAt) {
        this.harvestedAt = harvestedAt;
    }
}
