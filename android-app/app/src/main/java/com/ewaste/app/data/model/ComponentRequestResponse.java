package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ComponentRequestResponse implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("businessId")
    private Long businessId;

    @SerializedName("businessName")
    private String businessName;

    @SerializedName("componentId")
    private Long componentId;

    @SerializedName("componentName")
    private String componentName;

    @SerializedName("componentType")
    private String componentType;

    @SerializedName("componentCondition")
    private String componentCondition;

    @SerializedName("status")
    private String status; // PENDING, APPROVED, FULFILLED, REJECTED

    @SerializedName("requestedAt")
    private String requestedAt;

    @SerializedName("originalItemId")
    private Long originalItemId;

    @SerializedName("originalItemDescription")
    private String originalItemDescription;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("facilityName")
    private String facilityName;

    public Long getId() { return id; }
    public Long getBusinessId() { return businessId; }
    public String getBusinessName() { return businessName; }
    public Long getComponentId() { return componentId; }
    public String getComponentName() { return componentName; }
    public String getComponentType() { return componentType; }
    public String getComponentCondition() { return componentCondition; }
    public String getStatus() { return status; }
    public String getRequestedAt() { return requestedAt; }
    public Long getOriginalItemId() { return originalItemId; }
    public String getOriginalItemDescription() { return originalItemDescription; }
    public String getCategoryName() { return categoryName; }
    public String getFacilityName() { return facilityName; }
}
