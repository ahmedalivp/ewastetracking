package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class HazardousMaterialResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("ewasteItemId")
    private Long ewasteItemId;

    @SerializedName("type")
    private String type;

    @SerializedName("diversionStatus")
    private String diversionStatus; // FLAGGED, DIVERTED

    public Long getId() { return id; }
    public Long getEwasteItemId() { return ewasteItemId; }
    public String getType() { return type; }
    public String getDiversionStatus() { return diversionStatus; }
}
