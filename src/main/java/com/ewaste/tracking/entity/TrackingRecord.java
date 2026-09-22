package com.ewaste.tracking.entity;

import com.ewaste.tracking.enums.EntityType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * OOP PRINCIPLE: ENCAPSULATION & IMMUTABILITY (AUDIT TRAIL)
 * 
 * Immutable tracking record generated whenever the lifecycle status of an e-waste
 * item or harvested component changes. Provides non-repudiation and lifecycle provenance.
 * Notice there are intentionally NO setters for audit integrity once constructed.
 */
@Entity
@Table(name = "tracking_records")
public class TrackingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 1000)
    private String notes;

    public TrackingRecord() {
        this.timestamp = LocalDateTime.now();
    }

    public TrackingRecord(EntityType entityType, Long entityId, String status, User actor, String notes) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.status = status;
        this.actor = actor;
        this.notes = notes;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getStatus() {
        return status;
    }

    public User getActor() {
        return actor;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getNotes() {
        return notes;
    }
}
