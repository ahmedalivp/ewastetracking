package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class FlagHazardousMaterialRequest {
    @SerializedName("type")
    private String type; // BATTERY, MERCURY, LEAD, OTHER

    public FlagHazardousMaterialRequest(String type) {
        this.type = type;
    }

    public String getType() { return type; }
}
