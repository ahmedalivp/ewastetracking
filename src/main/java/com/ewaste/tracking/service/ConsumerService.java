package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.consumer.*;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines consumer actions: e-waste submissions using the Builder pattern,
 * tracking item lifecycle timelines, checking credit rewards, and redeeming perks.
 */
public interface ConsumerService {

    /**
     * Submits an e-waste item using the hand-written EWasteItemBuilder.
     *
     * @param consumerEmail authenticated consumer's email
     * @param request       submission details
     * @return EWasteItemResponse describing the submitted item
     */
    EWasteItemResponse submitEWasteItem(String consumerEmail, SubmitEWasteItemRequest request);

    List<EWasteItemResponse> getMySubmissions(String consumerEmail);

    List<TrackingRecordResponse> getItemTrackingTimeline(String consumerEmail, Long itemId);

    List<CreditResponse> getMyCredits(String consumerEmail);

    CreditRedemptionResponse redeemCredits(String consumerEmail, RedeemCreditRequest request);
}
