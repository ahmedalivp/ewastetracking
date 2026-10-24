package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.FacilityStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityStaffRepository extends JpaRepository<FacilityStaff, Long> {
    Optional<FacilityStaff> findByEmail(String email);
    List<FacilityStaff> findByFacilityId(Long facilityId);
    List<FacilityStaff> findByFacilityVerifiedByAdmin(boolean verifiedByAdmin);
}
