package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.ComponentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentRequestRepository extends JpaRepository<ComponentRequest, Long> {
    List<ComponentRequest> findByBusinessIdOrderByRequestedAtDesc(Long businessId);
    List<ComponentRequest> findAllByOrderByRequestedAtDesc();
}
