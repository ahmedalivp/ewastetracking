package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class TrackingRecordResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("entityType")
    private String entityType; // EWASTE_ITEM, COMPONENT

    @SerializedName("entityId")
    private Long entityId;

    @SerializedName("status")
    private String status;

    @SerializedName("actorId")
    private Long actorId;

    @SerializedName("actorName")
    private String actorName;

    @SerializedName("actorEmail")
    private String actorEmail;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("notes")
    private String notes;

    public Long getId() { return id; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getStatus() { return status; }
    public Long getActorId() { return actorId; }
    public String getActorName() { return actorName; }
    public String getActorEmail() { return actorEmail; }
    public String getTimestamp() { return timestamp; }
    public String getNotes() { return notes; }
}
