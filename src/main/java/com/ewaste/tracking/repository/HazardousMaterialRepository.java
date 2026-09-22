package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.HazardousMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HazardousMaterialRepository extends JpaRepository<HazardousMaterial, Long> {
    List<HazardousMaterial> findByEwasteItemId(Long ewasteItemId);
}
