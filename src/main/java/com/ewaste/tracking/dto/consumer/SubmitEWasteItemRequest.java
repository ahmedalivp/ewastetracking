package com.ewaste.tracking.dto.consumer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubmitEWasteItemRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Long dropOffPointId;

    @NotBlank(message = "Device description is required")
    private String deviceDescription;

    public SubmitEWasteItemRequest() {}

    public SubmitEWasteItemRequest(Long categoryId, Long dropOffPointId, String deviceDescription) {
        this.categoryId = categoryId;
        this.dropOffPointId = dropOffPointId;
        this.deviceDescription = deviceDescription;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getDropOffPointId() {
        return dropOffPointId;
    }

    public void setDropOffPointId(Long dropOffPointId) {
        this.dropOffPointId = dropOffPointId;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }
}
