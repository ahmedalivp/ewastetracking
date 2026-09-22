package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.DropOffPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DropOffPointRepository extends JpaRepository<DropOffPoint, Long> {
    List<DropOffPoint> findByFacilityId(Long facilityId);
}
