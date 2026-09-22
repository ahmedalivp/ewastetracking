package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.RecyclingFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecyclingFacilityRepository extends JpaRepository<RecyclingFacility, Long> {
    List<RecyclingFacility> findByVerifiedByAdmin(boolean verifiedByAdmin);
}
