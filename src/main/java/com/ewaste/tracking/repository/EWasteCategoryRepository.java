package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.EWasteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EWasteCategoryRepository extends JpaRepository<EWasteCategory, Long> {
    Optional<EWasteCategory> findByNameIgnoreCase(String name);
}
