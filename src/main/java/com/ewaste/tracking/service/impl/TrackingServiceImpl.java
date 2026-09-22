package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.consumer.TrackingRecordResponse;
import com.ewaste.tracking.entity.TrackingRecord;
import com.ewaste.tracking.entity.User;
import com.ewaste.tracking.enums.EntityType;
import com.ewaste.tracking.repository.TrackingRecordRepository;
import com.ewaste.tracking.service.TrackingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLE: ABSTRACTION (Concrete Implementation)
 * 
 * Implements the TrackingService contract, ensuring that tracking records
 * are created as an immutable audit trail and never exposed to updates or deletes.
 */
@Service
public class TrackingServiceImpl implements TrackingService {

    private final TrackingRecordRepository trackingRecordRepository;

    public TrackingServiceImpl(TrackingRecordRepository trackingRecordRepository) {
        this.trackingRecordRepository = trackingRecordRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public TrackingRecord recordTracking(EntityType entityType, Long entityId, String status, User actor, String notes) {
        TrackingRecord record = new TrackingRecord(entityType, entityId, status, actor, notes);
        return trackingRecordRepository.save(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingRecordResponse> getTimeline(EntityType entityType, Long entityId) {
        List<TrackingRecord> records = trackingRecordRepository.findByEntityTypeAndEntityIdOrderByTimestampAsc(entityType, entityId);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TrackingRecordResponse mapToResponse(TrackingRecord record) {
        Long actorId = record.getActor() != null ? record.getActor().getId() : null;
        String actorName = record.getActor() != null ? record.getActor().getFullName() : "System Automated";
        String actorEmail = record.getActor() != null ? record.getActor().getEmail() : "system@ewaste.org";

        return new TrackingRecordResponse(
                record.getId(),
                record.getEntityType(),
                record.getEntityId(),
                record.getStatus(),
                actorId,
                actorName,
                actorEmail,
                record.getTimestamp(),
                record.getNotes()
        );
    }
}
