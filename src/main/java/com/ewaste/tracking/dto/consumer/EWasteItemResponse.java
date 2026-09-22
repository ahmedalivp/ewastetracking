package com.ewaste.tracking.dto.consumer;

import com.ewaste.tracking.enums.EWasteStatus;
import java.time.LocalDateTime;

public class EWasteItemResponse {

    private Long id;
    private Long consumerId;
    private String consumerName;
    private Long categoryId;
    private String categoryName;
    private Long dropOffPointId;
    private String dropOffPointLabel;
    private String deviceDescription;
    private EWasteStatus status;
    private LocalDateTime submittedAt;
    private int componentCount;
    private int hazardousMaterialCount;

    public EWasteItemResponse() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(Long consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getDropOffPointId() {
        return dropOffPointId;
    }

    public void setDropOffPointId(Long dropOffPointId) {
        this.dropOffPointId = dropOffPointId;
    }

    public String getDropOffPointLabel() {
        return dropOffPointLabel;
    }

    public void setDropOffPointLabel(String dropOffPointLabel) {
        this.dropOffPointLabel = dropOffPointLabel;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }

    public EWasteStatus getStatus() {
        return status;
    }

    public void setStatus(EWasteStatus status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
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
