package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.Component;
import com.ewaste.tracking.enums.ComponentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    List<Component> findByStatus(ComponentStatus status);
    List<Component> findByEwasteItemCategoryNameIgnoreCaseAndStatus(String categoryName, ComponentStatus status);
    List<Component> findByEwasteItemId(Long ewasteItemId);
}
