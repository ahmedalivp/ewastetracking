package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class EWasteItemResponse implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("consumerId")
    private Long consumerId;

    @SerializedName("consumerName")
    private String consumerName;

    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("dropOffPointId")
    private Long dropOffPointId;

    @SerializedName("dropOffPointLabel")
    private String dropOffPointLabel;

    @SerializedName("deviceDescription")
    private String deviceDescription;

    @SerializedName("status")
    private String status; // SUBMITTED, DROPPED_OFF, RECEIVED, CATEGORIZED, PROCESSED, COMPLETED

    @SerializedName("submittedAt")
    private String submittedAt;

    @SerializedName("componentCount")
    private int componentCount;

    @SerializedName("hazardousMaterialCount")
    private int hazardousMaterialCount;

    public EWasteItemResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConsumerId() { return consumerId; }
    public void setConsumerId(Long consumerId) { this.consumerId = consumerId; }

    public String getConsumerName() { return consumerName; }
    public void setConsumerName(String consumerName) { this.consumerName = consumerName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getDropOffPointId() { return dropOffPointId; }
    public void setDropOffPointId(Long dropOffPointId) { this.dropOffPointId = dropOffPointId; }

    public String getDropOffPointLabel() { return dropOffPointLabel; }
    public void setDropOffPointLabel(String dropOffPointLabel) { this.dropOffPointLabel = dropOffPointLabel; }

    public String getDeviceDescription() { return deviceDescription; }
    public void setDeviceDescription(String deviceDescription) { this.deviceDescription = deviceDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }

    public int getComponentCount() { return componentCount; }
    public void setComponentCount(int componentCount) { this.componentCount = componentCount; }

    public int getHazardousMaterialCount() { return hazardousMaterialCount; }
    public void setHazardousMaterialCount(int hazardousMaterialCount) { this.hazardousMaterialCount = hazardousMaterialCount; }
}
