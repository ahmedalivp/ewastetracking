package com.ewaste.tracking.dto.facility;

import com.ewaste.tracking.enums.HazardousType;
import jakarta.validation.constraints.NotNull;

public class FlagHazardousMaterialRequest {

    @NotNull(message = "Hazardous material type is required (BATTERY, MERCURY, LEAD, OTHER)")
    private HazardousType type;

    public FlagHazardousMaterialRequest() {}

    public FlagHazardousMaterialRequest(HazardousType type) {
        this.type = type;
    }

    public HazardousType getType() {
        return type;
    }

    public void setType(HazardousType type) {
        this.type = type;
    }
}
