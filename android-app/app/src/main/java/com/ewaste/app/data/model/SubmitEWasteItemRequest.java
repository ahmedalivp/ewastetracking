package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class SubmitEWasteItemRequest {
    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("dropOffPointId")
    private Long dropOffPointId;

    @SerializedName("deviceDescription")
    private String deviceDescription;

    public SubmitEWasteItemRequest(Long categoryId, Long dropOffPointId, String deviceDescription) {
        this.categoryId = categoryId;
        this.dropOffPointId = dropOffPointId;
        this.deviceDescription = deviceDescription;
    }

    public Long getCategoryId() { return categoryId; }
    public Long getDropOffPointId() { return dropOffPointId; }
    public String getDeviceDescription() { return deviceDescription; }
}
