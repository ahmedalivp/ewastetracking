package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.RecyclingCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecyclingCreditRepository extends JpaRepository<RecyclingCredit, Long> {
    List<RecyclingCredit> findByConsumerIdOrderByAwardedAtDesc(Long consumerId);
}
