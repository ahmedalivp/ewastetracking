package com.ewaste.tracking.dto.facility;

import com.ewaste.tracking.enums.ComponentCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HarvestComponentRequest {

    @NotBlank(message = "Component name is required")
    private String name;

    @NotBlank(message = "Component type is required (e.g., RAM, GPU, Screen, SSD)")
    private String type;

    @NotNull(message = "Condition is required (GOOD, FAIR, POOR)")
    private ComponentCondition condition;

    public HarvestComponentRequest() {}

    public HarvestComponentRequest(String name, String type, ComponentCondition condition) {
        this.name = name;
        this.type = type;
        this.condition = condition;
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
}
