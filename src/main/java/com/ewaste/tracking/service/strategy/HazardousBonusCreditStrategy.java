package com.ewaste.tracking.service.strategy;

import com.ewaste.tracking.entity.EWasteItem;
import org.springframework.stereotype.Component;

/**
 * OOP DESIGN PATTERN: STRATEGY IMPLEMENTATION (Hazardous Material Bonus Calculation)
 * 
 * Computes enhanced environmental stewardship credits. In addition to standard
 * base points and component reuse bonuses, it awards high-tier incentive points
 * for each toxic/hazardous material safely flagged and diverted from municipal dumps.
 */
@Component("hazardousBonusCreditStrategy")
public class HazardousBonusCreditStrategy implements CreditStrategy {

    private final StandardCreditStrategy standardCreditStrategy;
    private static final int PER_HAZARDOUS_BONUS = 25;

    public HazardousBonusCreditStrategy(StandardCreditStrategy standardCreditStrategy) {
        this.standardCreditStrategy = standardCreditStrategy;
    }

    @Override
    public int calculate(EWasteItem item) {
        if (item == null) {
            return 0;
        }

        // Calculate baseline points from standard strategy
        int basePoints = standardCreditStrategy.calculate(item);

        // Environmental protection bonus for every safely diverted/flagged hazardous element
        int hazardousCount = (item.getHazardousMaterials() != null) ? item.getHazardousMaterials().size() : 0;
        int hazardousBonus = hazardousCount * PER_HAZARDOUS_BONUS;

        return basePoints + hazardousBonus;
    }
}
