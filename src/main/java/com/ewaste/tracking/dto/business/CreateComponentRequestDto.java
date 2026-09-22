package com.ewaste.tracking.dto.business;

import jakarta.validation.constraints.NotNull;

public class CreateComponentRequestDto {

    @NotNull(message = "Component ID is required")
    private Long componentId;

    public CreateComponentRequestDto() {}

    public CreateComponentRequestDto(Long componentId) {
        this.componentId = componentId;
    }

    public Long getComponentId() {
        return componentId;
    }

    public void setComponentId(Long componentId) {
        this.componentId = componentId;
    }
}
