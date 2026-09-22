package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class CategoryResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return name != null ? name : "";
    }
}
