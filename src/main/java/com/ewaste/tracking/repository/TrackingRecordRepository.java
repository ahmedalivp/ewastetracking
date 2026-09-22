package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.TrackingRecord;
import com.ewaste.tracking.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingRecordRepository extends JpaRepository<TrackingRecord, Long> {
    List<TrackingRecord> findByEntityTypeAndEntityIdOrderByTimestampAsc(EntityType entityType, Long entityId);
}
