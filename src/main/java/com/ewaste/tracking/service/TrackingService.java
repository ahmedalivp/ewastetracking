package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.consumer.TrackingRecordResponse;
import com.ewaste.tracking.entity.TrackingRecord;
import com.ewaste.tracking.entity.User;
import com.ewaste.tracking.enums.EntityType;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines the public contract for creating and querying immutable audit trails.
 * Implementation details (database queries, transactional boundaries) are hidden
 * inside TrackingServiceImpl.
 */
public interface TrackingService {

    /**
     * Records a non-repudiable lifecycle transition audit record.
     *
     * @param entityType target entity type (EWASTE_ITEM or COMPONENT)
     * @param entityId   primary key of the entity
     * @param status     new lifecycle state
     * @param actor      user performing the state change
     * @param notes      contextual notes or description
     * @return persisted TrackingRecord
     */
    TrackingRecord recordTracking(EntityType entityType, Long entityId, String status, User actor, String notes);

    /**
     * Retrieves the chronological timeline history for an entity.
     *
     * @param entityType target entity type
     * @param entityId   primary key of the entity
     * @return ordered list of tracking record responses
     */
    List<TrackingRecordResponse> getTimeline(EntityType entityType, Long entityId);
}
