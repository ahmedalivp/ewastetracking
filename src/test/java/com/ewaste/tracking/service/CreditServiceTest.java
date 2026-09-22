package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.consumer.CreditRedemptionResponse;
import com.ewaste.tracking.entity.*;
import com.ewaste.tracking.enums.ComponentCondition;
import com.ewaste.tracking.enums.ComponentStatus;
import com.ewaste.tracking.enums.DiversionStatus;
import com.ewaste.tracking.enums.HazardousType;
import com.ewaste.tracking.exception.BadRequestException;
import com.ewaste.tracking.repository.ConsumerRepository;
import com.ewaste.tracking.repository.CreditRedemptionRepository;
import com.ewaste.tracking.repository.RecyclingCreditRepository;
import com.ewaste.tracking.service.impl.CreditServiceImpl;
import com.ewaste.tracking.service.strategy.CreditStrategy;
import com.ewaste.tracking.service.strategy.HazardousBonusCreditStrategy;
import com.ewaste.tracking.service.strategy.StandardCreditStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreditService & CreditStrategy Tests - Strategy Design Pattern")
class CreditServiceTest {

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private RecyclingCreditRepository recyclingCreditRepository;

    @Mock
    private CreditRedemptionRepository creditRedemptionRepository;

    private StandardCreditStrategy standardCreditStrategy;
    private HazardousBonusCreditStrategy hazardousBonusCreditStrategy;
    private CreditServiceImpl creditService;

    private Consumer consumer;
    private EWasteCategory laptopCategory;
    private EWasteCategory phoneCategory;
    private EWasteItem laptopItem;

    @BeforeEach
    void setUp() {
        standardCreditStrategy = new StandardCreditStrategy();
        hazardousBonusCreditStrategy = new HazardousBonusCreditStrategy(standardCreditStrategy);

        creditService = new CreditServiceImpl(
                standardCreditStrategy,
                hazardousBonusCreditStrategy,
                consumerRepository,
                recyclingCreditRepository,
                creditRedemptionRepository
        );

        consumer = new Consumer("Alice Green", "alice@example.com", "hash", 50);
        consumer.setId(1L);

        laptopCategory = new EWasteCategory("Laptop", "Portable computers");
        phoneCategory = new EWasteCategory("Smartphone", "Mobile cellular phones");

        laptopItem = new EWasteItem();
        laptopItem.setId(100L);
        laptopItem.setConsumer(consumer);
        laptopItem.setCategory(laptopCategory);
        laptopItem.setComponents(new ArrayList<>());
        laptopItem.setHazardousMaterials(new ArrayList<>());
    }

    @Test
    @DisplayName("StandardCreditStrategy should calculate base points plus component bonuses")
    void testStandardCreditStrategy_Calculation() {
        // Laptop base = 50. Adding 2 components = 50 + (2 * 10) = 70
        Component c1 = new Component(laptopItem, "8GB DDR4 RAM", "RAM", ComponentCondition.GOOD, ComponentStatus.AVAILABLE);
        Component c2 = new Component(laptopItem, "256GB NVMe SSD", "Storage", ComponentCondition.GOOD, ComponentStatus.AVAILABLE);
        laptopItem.getComponents().addAll(List.of(c1, c2));

        int points = standardCreditStrategy.calculate(laptopItem);
        assertEquals(70, points, "Laptop (50) + 2 components (20) should equal 70 credits");
    }

    @Test
    @DisplayName("HazardousBonusCreditStrategy should award standard points plus toxic material bonuses")
    void testHazardousBonusCreditStrategy_Calculation() {
        // Laptop base = 50. 1 component = 10. Total standard = 60.
        Component c1 = new Component(laptopItem, "LCD Panel", "Display", ComponentCondition.GOOD, ComponentStatus.AVAILABLE);
        laptopItem.getComponents().add(c1);

        // 2 hazardous materials = 2 * 25 = 50 bonus points. Total = 60 + 50 = 110.
        HazardousMaterial m1 = new HazardousMaterial(laptopItem, HazardousType.BATTERY, DiversionStatus.FLAGGED);
        HazardousMaterial m2 = new HazardousMaterial(laptopItem, HazardousType.MERCURY, DiversionStatus.FLAGGED);
        laptopItem.getHazardousMaterials().addAll(List.of(m1, m2));

        int points = hazardousBonusCreditStrategy.calculate(laptopItem);
        assertEquals(110, points, "Standard 60 + 2 hazardous materials (50) should equal 110 credits");
    }

    @Test
    @DisplayName("CreditService should dynamically select HazardousBonusCreditStrategy when hazardous items exist")
    void testCreditService_SelectStrategy_WithHazardous() {
        HazardousMaterial mat = new HazardousMaterial(laptopItem, HazardousType.LEAD, DiversionStatus.FLAGGED);
        laptopItem.getHazardousMaterials().add(mat);

        CreditStrategy selected = creditService.selectStrategy(laptopItem);
        assertInstanceOf(HazardousBonusCreditStrategy.class, selected);
    }

    @Test
    @DisplayName("CreditService should select StandardCreditStrategy when no hazardous items exist")
    void testCreditService_SelectStrategy_Standard() {
        CreditStrategy selected = creditService.selectStrategy(laptopItem);
        assertInstanceOf(StandardCreditStrategy.class, selected);
    }

    @Test
    @DisplayName("CreditService awardRecyclingCredits should update consumer balance and persist audit record")
    void testAwardRecyclingCredits_Success() {
        int initialBalance = consumer.getCreditBalance(); // 50
        // Laptop without components or hazardous = 50 base points
        int awarded = creditService.awardRecyclingCredits(laptopItem, "Device intake reward");

        assertEquals(50, awarded);
        assertEquals(initialBalance + 50, consumer.getCreditBalance());

        verify(consumerRepository, times(1)).save(consumer);
        verify(recyclingCreditRepository, times(1)).save(any(RecyclingCredit.class));
    }

    @Test
    @DisplayName("CreditService redeemCredits should deduct points and return remaining balance")
    void testRedeemCredits_Success() {
        when(creditRedemptionRepository.save(any(CreditRedemption.class))).thenAnswer(i -> {
            CreditRedemption r = i.getArgument(0);
            r.setId(99L);
            return r;
        });

        CreditRedemptionResponse response = creditService.redeemCredits(consumer, 30, "$10 Eco-Voucher");

        assertNotNull(response);
        assertEquals(30, response.getAmount());
        assertEquals("$10 Eco-Voucher", response.getRedeemedFor());
        assertEquals(20, response.getRemainingBalance());
        assertEquals(20, consumer.getCreditBalance());

        verify(consumerRepository, times(1)).save(consumer);
        verify(creditRedemptionRepository, times(1)).save(any(CreditRedemption.class));
    }

    @Test
    @DisplayName("CreditService redeemCredits should throw BadRequestException when balance is insufficient")
    void testRedeemCredits_InsufficientBalance() {
        assertThrows(BadRequestException.class, () ->
                creditService.redeemCredits(consumer, 100, "Too expensive reward")
        );
        verify(consumerRepository, never()).save(any());
        verify(creditRedemptionRepository, never()).save(any());
    }
}
