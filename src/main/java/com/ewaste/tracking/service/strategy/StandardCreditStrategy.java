package com.ewaste.tracking.service.strategy;

import com.ewaste.tracking.entity.EWasteItem;
import org.springframework.stereotype.Component;

/**
 * OOP DESIGN PATTERN: STRATEGY IMPLEMENTATION (Standard Credit Calculation)
 * 
 * Computes standard reward credits based on device classification baseline
 * plus incremental bonuses for each component harvested for circular reuse.
 */
@Component("standardCreditStrategy")
public class StandardCreditStrategy implements CreditStrategy {

    private static final int BASE_LAPTOP = 50;
    private static final int BASE_SMARTPHONE = 30;
    private static final int BASE_TV = 40;
    private static final int BASE_BATTERY = 20;
    private static final int BASE_DEFAULT = 15;
    private static final int PER_COMPONENT_BONUS = 10;

    @Override
    public int calculate(EWasteItem item) {
        if (item == null) {
            return 0;
        }

        int points = 0;

        // Base points determined by category
        if (item.getCategory() != null && item.getCategory().getName() != null) {
            String category = item.getCategory().getName().toUpperCase();
            if (category.contains("LAPTOP")) {
                points += BASE_LAPTOP;
            } else if (category.contains("SMARTPHONE") || category.contains("PHONE")) {
                points += BASE_SMARTPHONE;
            } else if (category.contains("TV") || category.contains("TELEVISION")) {
                points += BASE_TV;
            } else if (category.contains("BATTERY")) {
                points += BASE_BATTERY;
            } else {
                points += BASE_DEFAULT;
            }
        } else {
            points += BASE_DEFAULT;
        }

        // Additional points for each harvested component
        int componentCount = (item.getComponents() != null) ? item.getComponents().size() : 0;
        points += componentCount * PER_COMPONENT_BONUS;

        return points;
    }
}
