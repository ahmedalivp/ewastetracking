package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class HarvestComponentRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("condition")
    private String condition; // GOOD, FAIR, POOR

    public HarvestComponentRequest(String name, String type, String condition) {
        this.name = name;
        this.type = type;
        this.condition = condition;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getCondition() { return condition; }
}
