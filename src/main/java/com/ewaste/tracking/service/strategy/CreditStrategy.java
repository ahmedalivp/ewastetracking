package com.ewaste.tracking.service.strategy;

import com.ewaste.tracking.entity.EWasteItem;

/**
 * OOP DESIGN PATTERN: STRATEGY PATTERN
 * 
 * Defines a family of interchangeable recycling credit calculation algorithms.
 * Encapsulates the credit evaluation logic so that each calculation strategy
 * can vary independently from clients that use it.
 */
public interface CreditStrategy {

    /**
     * Calculates the recycling credit incentive points awarded to a consumer
     * based on an e-waste item's attributes, category, components, or hazardous items.
     *
     * @param item the processed e-waste item
     * @return non-negative calculated credit points
     */
    int calculate(EWasteItem item);
}
