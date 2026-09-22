package com.ewaste.tracking.dto.facility;

import com.ewaste.tracking.enums.DiversionStatus;
import com.ewaste.tracking.enums.HazardousType;

public class HazardousMaterialResponse {

    private Long id;
    private Long ewasteItemId;
    private HazardousType type;
    private DiversionStatus diversionStatus;

    public HazardousMaterialResponse() {}

    public HazardousMaterialResponse(Long id, Long ewasteItemId, HazardousType type, DiversionStatus diversionStatus) {
        this.id = id;
        this.ewasteItemId = ewasteItemId;
        this.type = type;
        this.diversionStatus = diversionStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEwasteItemId() {
        return ewasteItemId;
    }

    public void setEwasteItemId(Long ewasteItemId) {
        this.ewasteItemId = ewasteItemId;
    }

    public HazardousType getType() {
        return type;
    }

    public void setType(HazardousType type) {
        this.type = type;
    }

    public DiversionStatus getDiversionStatus() {
        return diversionStatus;
    }

    public void setDiversionStatus(DiversionStatus diversionStatus) {
        this.diversionStatus = diversionStatus;
    }
}
