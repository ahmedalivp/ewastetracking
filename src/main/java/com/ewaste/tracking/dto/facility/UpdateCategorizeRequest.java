package com.ewaste.tracking.dto.facility;

import jakarta.validation.constraints.NotNull;

public class UpdateCategorizeRequest {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String triageNotes;

    public UpdateCategorizeRequest() {}

    public UpdateCategorizeRequest(Long categoryId, String triageNotes) {
        this.categoryId = categoryId;
        this.triageNotes = triageNotes;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTriageNotes() {
        return triageNotes;
    }

    public void setTriageNotes(String triageNotes) {
        this.triageNotes = triageNotes;
    }
}
