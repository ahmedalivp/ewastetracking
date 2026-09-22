package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.consumer.CreditRedemptionResponse;
import com.ewaste.tracking.dto.consumer.CreditResponse;
import com.ewaste.tracking.entity.Consumer;
import com.ewaste.tracking.entity.CreditRedemption;
import com.ewaste.tracking.entity.EWasteItem;
import com.ewaste.tracking.entity.RecyclingCredit;
import com.ewaste.tracking.exception.BadRequestException;
import com.ewaste.tracking.repository.ConsumerRepository;
import com.ewaste.tracking.repository.CreditRedemptionRepository;
import com.ewaste.tracking.repository.RecyclingCreditRepository;
import com.ewaste.tracking.service.CreditService;
import com.ewaste.tracking.service.strategy.CreditStrategy;
import com.ewaste.tracking.service.strategy.HazardousBonusCreditStrategy;
import com.ewaste.tracking.service.strategy.StandardCreditStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * OOP PRINCIPLE: ABSTRACTION & STRATEGY PATTERN IN ACTION
 * 
 * - Abstraction: Fulfills the CreditService interface contract.
 * - Strategy Pattern: Dynamically delegates credit computation to the appropriate
 *   CreditStrategy algorithm depending on whether toxic/hazardous materials are present.
 */
@Service
public class CreditServiceImpl implements CreditService {

    private final StandardCreditStrategy standardCreditStrategy;
    private final HazardousBonusCreditStrategy hazardousBonusCreditStrategy;
    private final ConsumerRepository consumerRepository;
    private final RecyclingCreditRepository recyclingCreditRepository;
    private final CreditRedemptionRepository creditRedemptionRepository;

    public CreditServiceImpl(StandardCreditStrategy standardCreditStrategy,
                             HazardousBonusCreditStrategy hazardousBonusCreditStrategy,
                             ConsumerRepository consumerRepository,
                             RecyclingCreditRepository recyclingCreditRepository,
                             CreditRedemptionRepository creditRedemptionRepository) {
        this.standardCreditStrategy = standardCreditStrategy;
        this.hazardousBonusCreditStrategy = hazardousBonusCreditStrategy;
        this.consumerRepository = consumerRepository;
        this.recyclingCreditRepository = recyclingCreditRepository;
        this.creditRedemptionRepository = creditRedemptionRepository;
    }

    /**
     * OOP DESIGN PATTERN: STRATEGY SELECTION
     * 
     * Selects HazardousBonusCreditStrategy if hazardous materials were safely diverted,
     * otherwise falls back to the StandardCreditStrategy.
     */
    @Override
    public CreditStrategy selectStrategy(EWasteItem item) {
        if (item != null && item.getHazardousMaterials() != null && !item.getHazardousMaterials().isEmpty()) {
            return hazardousBonusCreditStrategy;
        }
        return standardCreditStrategy;
    }

    @Override
    @Transactional
    public int awardRecyclingCredits(EWasteItem item, String reason) {
        if (item == null || item.getConsumer() == null) {
            return 0;
        }

        // Dynamically execute chosen strategy
        CreditStrategy strategy = selectStrategy(item);
        int points = strategy.calculate(item);

        if (points > 0) {
            Consumer consumer = item.getConsumer();
            consumer.addCredits(points);
            consumerRepository.save(consumer);

            RecyclingCredit creditRecord = new RecyclingCredit(consumer, points, reason);
            recyclingCreditRepository.save(creditRecord);
        }

        return points;
    }

    @Override
    @Transactional
    public CreditRedemptionResponse redeemCredits(Consumer consumer, int amount, String redeemedFor) {
        if (amount <= 0) {
            throw new BadRequestException("Redemption amount must be greater than zero");
        }
        if (consumer.getCreditBalance() < amount) {
            throw new BadRequestException("Insufficient credit balance: available " + consumer.getCreditBalance() + ", required " + amount);
        }

        consumer.deductCredits(amount);
        consumerRepository.save(consumer);

        CreditRedemption redemption = new CreditRedemption(consumer, amount, redeemedFor);
        CreditRedemption savedRedemption = creditRedemptionRepository.save(redemption);

        return new CreditRedemptionResponse(
                savedRedemption.getId(),
                savedRedemption.getAmount(),
                savedRedemption.getRedeemedFor(),
                savedRedemption.getRedeemedAt(),
                consumer.getCreditBalance()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditResponse> getConsumerCredits(Long consumerId) {
        return recyclingCreditRepository.findByConsumerIdOrderByAwardedAtDesc(consumerId).stream()
                .map(credit -> new CreditResponse(credit.getId(), credit.getAmount(), credit.getReason(), credit.getAwardedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditRedemptionResponse> getConsumerRedemptions(Long consumerId) {
        Consumer consumer = consumerRepository.findById(consumerId).orElse(null);
        int currentBalance = (consumer != null) ? consumer.getCreditBalance() : 0;

        return creditRedemptionRepository.findByConsumerIdOrderByRedeemedAtDesc(consumerId).stream()
                .map(redemption -> new CreditRedemptionResponse(
                        redemption.getId(),
                        redemption.getAmount(),
                        redemption.getRedeemedFor(),
                        redemption.getRedeemedAt(),
                        currentBalance
                ))
                .collect(Collectors.toList());
    }
}
