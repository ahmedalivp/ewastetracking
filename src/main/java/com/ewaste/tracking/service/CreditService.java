package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.consumer.CreditRedemptionResponse;
import com.ewaste.tracking.dto.consumer.CreditResponse;
import com.ewaste.tracking.entity.Consumer;
import com.ewaste.tracking.entity.EWasteItem;
import com.ewaste.tracking.service.strategy.CreditStrategy;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines business operations for calculating incentive credits using the
 * Strategy pattern, awarding credits upon recycling events, and redeeming rewards.
 */
public interface CreditService {

    /**
     * Dynamically selects the appropriate CreditStrategy depending on the item's properties.
     *
     * @param item the processed e-waste item
     * @return concrete CreditStrategy instance
     */
    CreditStrategy selectStrategy(EWasteItem item);

    /**
     * Calculates and awards recycling credits to the item's consumer owner.
     *
     * @param item   the processed e-waste item
     * @param reason context description for the awarded credit
     * @return points awarded
     */
    int awardRecyclingCredits(EWasteItem item, String reason);

    /**
     * Redeems credits for eco-vouchers or perks.
     *
     * @param consumer    the consumer redeeming points
     * @param amount      quantity of credits to redeem
     * @param redeemedFor perk description
     * @return CreditRedemptionResponse detailing the outcome
     */
    CreditRedemptionResponse redeemCredits(Consumer consumer, int amount, String redeemedFor);

    /**
     * Fetches awarded credit history for a consumer.
     *
     * @param consumerId consumer's primary key
     * @return list of credit history records
     */
    List<CreditResponse> getConsumerCredits(Long consumerId);

    /**
     * Fetches redemption history for a consumer.
     *
     * @param consumerId consumer's primary key
     * @return list of redemptions
     */
    List<CreditRedemptionResponse> getConsumerRedemptions(Long consumerId);
}
