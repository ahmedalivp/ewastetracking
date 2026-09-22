package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ComponentResponse implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("ewasteItemId")
    private Long ewasteItemId;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("condition")
    private String condition; // GOOD, FAIR, POOR

    @SerializedName("status")
    private String status; // HARVESTED, AVAILABLE, REQUESTED, FULFILLED

    @SerializedName("harvestedAt")
    private String harvestedAt;

    public Long getId() { return id; }
    public Long getEwasteItemId() { return ewasteItemId; }
    public String getCategoryName() { return categoryName; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCondition() { return condition; }
    public String getStatus() { return status; }
    public String getHarvestedAt() { return harvestedAt; }
}
