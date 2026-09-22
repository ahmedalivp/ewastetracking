package com.ewaste.tracking.repository;

import com.ewaste.tracking.entity.CreditRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRedemptionRepository extends JpaRepository<CreditRedemption, Long> {
    List<CreditRedemption> findByConsumerIdOrderByRedeemedAtDesc(Long consumerId);
}
