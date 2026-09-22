package com.ewaste.tracking.dto.facility;

import com.ewaste.tracking.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateComponentRequestStatusRequest {

    @NotNull(message = "Request status is required (APPROVED, FULFILLED, REJECTED)")
    private RequestStatus status;

    public UpdateComponentRequestStatusRequest() {}

    public UpdateComponentRequestStatusRequest(RequestStatus status) {
        this.status = status;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
