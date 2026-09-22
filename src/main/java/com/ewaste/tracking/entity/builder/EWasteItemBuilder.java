package com.ewaste.tracking.entity.builder;

import com.ewaste.tracking.entity.Consumer;
import com.ewaste.tracking.entity.DropOffPoint;
import com.ewaste.tracking.entity.EWasteCategory;
import com.ewaste.tracking.entity.EWasteItem;
import com.ewaste.tracking.enums.EWasteStatus;

import java.time.LocalDateTime;

/**
 * OOP DESIGN PATTERN: BUILDER PATTERN (HAND-WRITTEN)
 * 
 * Demonstrates the classic GoF Builder design pattern:
 * - Separates the complex construction of an EWasteItem from its representation.
 * - Allows step-by-step assembly of required and optional properties (e.g., dropOffPoint is optional).
 * - Enforces object integrity and invariant checking before instantiation in the build() method.
 * - Implemented manually without Lombok to showcase deep understanding of the pattern.
 */
public class EWasteItemBuilder {

    private Consumer consumer;
    private EWasteCategory category;
    private DropOffPoint dropOffPoint;
    private String deviceDescription;
    private EWasteStatus status;
    private LocalDateTime submittedAt;

    public EWasteItemBuilder() {
        // Default lifecycle status and timestamp
        this.status = EWasteStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }

    public EWasteItemBuilder consumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public EWasteItemBuilder category(EWasteCategory category) {
        this.category = category;
        return this;
    }

    public EWasteItemBuilder dropOffPoint(DropOffPoint dropOffPoint) {
        this.dropOffPoint = dropOffPoint;
        return this;
    }

    public EWasteItemBuilder deviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
        return this;
    }

    public EWasteItemBuilder status(EWasteStatus status) {
        this.status = status;
        return this;
    }

    public EWasteItemBuilder submittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
        return this;
    }

    /**
     * Validates invariants and constructs the final immutable/consistent EWasteItem instance.
     *
     * @return Fully initialized EWasteItem
     * @throws IllegalStateException if mandatory attributes are missing
     */
    public EWasteItem build() {
        if (this.consumer == null) {
            throw new IllegalStateException("EWasteItem construction failed: consumer must not be null");
        }
        if (this.category == null) {
            throw new IllegalStateException("EWasteItem construction failed: category must not be null");
        }
        if (this.deviceDescription == null || this.deviceDescription.trim().isEmpty()) {
            throw new IllegalStateException("EWasteItem construction failed: deviceDescription must not be blank");
        }

        EWasteStatus finalStatus = (this.status != null) ? this.status : EWasteStatus.SUBMITTED;
        LocalDateTime finalTimestamp = (this.submittedAt != null) ? this.submittedAt : LocalDateTime.now();

        return new EWasteItem(
                this.consumer,
                this.category,
                this.dropOffPoint,
                this.deviceDescription.trim(),
                finalStatus,
                finalTimestamp
        );
    }
}
