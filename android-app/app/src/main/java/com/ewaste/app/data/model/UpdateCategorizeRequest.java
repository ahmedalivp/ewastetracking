package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateCategorizeRequest {
    @SerializedName("categoryId")
    private Long categoryId;

    @SerializedName("triageNotes")
    private String triageNotes;

    public UpdateCategorizeRequest(Long categoryId, String triageNotes) {
        this.categoryId = categoryId;
        this.triageNotes = triageNotes;
    }

    public Long getCategoryId() { return categoryId; }
    public String getTriageNotes() { return triageNotes; }
}
