package com.ewaste.tracking.dto.business;

import com.ewaste.tracking.enums.ComponentCondition;
import com.ewaste.tracking.enums.RequestStatus;
import java.time.LocalDateTime;

public class ComponentRequestResponse {

    private Long id;
    private Long businessId;
    private String businessName;
    private Long componentId;
    private String componentName;
    private String componentType;
    private ComponentCondition componentCondition;
    private RequestStatus status;
    private LocalDateTime requestedAt;

    public ComponentRequestResponse() {}

    public ComponentRequestResponse(Long id, Long businessId, String businessName,
                                    Long componentId, String componentName, String componentType,
                                    ComponentCondition componentCondition, RequestStatus status,
                                    LocalDateTime requestedAt) {
        this.id = id;
        this.businessId = businessId;
        this.businessName = businessName;
        this.componentId = componentId;
        this.componentName = componentName;
        this.componentType = componentType;
        this.componentCondition = componentCondition;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Long businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public ComponentCondition getComponentCondition() {
        return componentCondition;
    }

    public void setComponentCondition(ComponentCondition componentCondition) {
        this.componentCondition = componentCondition;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}
