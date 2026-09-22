package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.EWasteItem;
import com.ewaste.tracking.enums.EWasteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EWasteItemRepository extends JpaRepository<EWasteItem, Long> {
    List<EWasteItem> findByConsumerIdOrderBySubmittedAtDesc(Long consumerId);
    List<EWasteItem> findByStatusOrderBySubmittedAtDesc(EWasteStatus status);
    List<EWasteItem> findAllByOrderBySubmittedAtDesc();
}
