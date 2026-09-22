package com.ewaste.tracking.dto.consumer;

import com.ewaste.tracking.enums.EntityType;
import java.time.LocalDateTime;

public class TrackingRecordResponse {

    private Long id;
    private EntityType entityType;
    private Long entityId;
    private String status;
    private Long actorId;
    private String actorName;
    private String actorEmail;
    private LocalDateTime timestamp;
    private String notes;

    public TrackingRecordResponse() {}

    public TrackingRecordResponse(Long id, EntityType entityType, Long entityId, String status,
                                  Long actorId, String actorName, String actorEmail,
                                  LocalDateTime timestamp, String notes) {
        this.id = id;
        this.entityType = entityType;
        this.entityId = entityId;
        this.status = status;
        this.actorId = actorId;
        this.actorName = actorName;
        this.actorEmail = actorEmail;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
