package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateComponentRequestDto {
    @SerializedName("componentId")
    private Long componentId;

    public CreateComponentRequestDto(Long componentId) {
        this.componentId = componentId;
    }

    public Long getComponentId() { return componentId; }
}
