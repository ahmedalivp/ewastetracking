package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateComponentRequestStatusRequest {
    @SerializedName("status")
    private String status; // APPROVED, FULFILLED, REJECTED

    public UpdateComponentRequestStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() { return status; }
}
