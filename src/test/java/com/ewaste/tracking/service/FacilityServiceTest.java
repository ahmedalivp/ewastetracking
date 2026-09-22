package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.consumer.EWasteItemResponse;
import com.ewaste.tracking.dto.facility.*;
import com.ewaste.tracking.entity.*;
import com.ewaste.tracking.enums.*;
import com.ewaste.tracking.repository.*;
import com.ewaste.tracking.service.impl.FacilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityService Tests - Service Layer & Composition & Tracking")
class FacilityServiceTest {

    @Mock
    private FacilityStaffRepository staffRepository;

    @Mock
    private EWasteItemRepository ewasteItemRepository;

    @Mock
    private EWasteCategoryRepository categoryRepository;

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private HazardousMaterialRepository hazardousMaterialRepository;

    @Mock
    private ComponentRequestRepository componentRequestRepository;

    @Mock
    private TrackingService trackingService;

    @Mock
    private CreditService creditService;

    @InjectMocks
    private FacilityServiceImpl facilityService;

    private FacilityStaff staff;
    private RecyclingFacility facility;
    private EWasteItem item;
    private EWasteCategory laptopCategory;

    @BeforeEach
    void setUp() {
        facility = new RecyclingFacility("GreenTech Center", "123 Green Rd", 37.7, -122.4, true);
        facility.setId(10L);

        staff = new FacilityStaff("John Tech", "staff@greentech.org", "pass", facility);
        staff.setId(20L);

        laptopCategory = new EWasteCategory("Laptop", "Laptops & Notebooks");
        laptopCategory.setId(1L);

        item = new EWasteItem();
        item.setId(100L);
        item.setStatus(EWasteStatus.SUBMITTED);
        item.setCategory(laptopCategory);
        item.setDeviceDescription("Old ThinkPad T480");
        item.setComponents(new ArrayList<>());
        item.setHazardousMaterials(new ArrayList<>());
    }

    @Test
    @DisplayName("receiveItem should update status to RECEIVED and create immutable tracking record")
    void testReceiveItem_Success() {
        when(staffRepository.findByEmail("staff@greentech.org")).thenReturn(Optional.of(staff));
        when(ewasteItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(ewasteItemRepository.save(any(EWasteItem.class))).thenAnswer(i -> i.getArgument(0));

        EWasteItemResponse response = facilityService.receiveItem("staff@greentech.org", 100L);

        assertNotNull(response);
        assertEquals(EWasteStatus.RECEIVED, response.getStatus());

        verify(trackingService, times(1)).recordTracking(
                eq(EntityType.EWASTE_ITEM),
                eq(100L),
                eq(EWasteStatus.RECEIVED.name()),
                eq(staff),
                anyString()
        );
    }

    @Test
    @DisplayName("harvestComponent should attach component, advance status to PROCESSED, log tracking, and auto-award credits")
    void testHarvestComponent_Success() {
        when(staffRepository.findByEmail("staff@greentech.org")).thenReturn(Optional.of(staff));
        when(ewasteItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(ewasteItemRepository.save(any(EWasteItem.class))).thenAnswer(i -> i.getArgument(0));
        when(componentRepository.save(any(Component.class))).thenAnswer(i -> {
            Component c = i.getArgument(0);
            c.setId(500L);
            return c;
        });

        HarvestComponentRequest request = new HarvestComponentRequest("16GB RAM DDR4", "RAM", ComponentCondition.GOOD);
        ComponentResponse response = facilityService.harvestComponent("staff@greentech.org", 100L, request);

        assertNotNull(response);
        assertEquals(500L, response.getId());
        assertEquals("16GB RAM DDR4", response.getName());
        assertEquals("RAM", response.getType());
        assertEquals(ComponentCondition.GOOD, response.getCondition());
        assertEquals(ComponentStatus.AVAILABLE, response.getStatus());
        assertEquals(EWasteStatus.PROCESSED, item.getStatus());

        // Verify tracking recorded for both component and e-waste item
        verify(trackingService, times(1)).recordTracking(
                eq(EntityType.COMPONENT),
                eq(500L),
                eq(ComponentStatus.AVAILABLE.name()),
                eq(staff),
                anyString()
        );

        verify(trackingService, times(1)).recordTracking(
                eq(EntityType.EWASTE_ITEM),
                eq(100L),
                eq(EWasteStatus.PROCESSED.name()),
                eq(staff),
                anyString()
        );

        // Verify credit calculation and award triggered
        verify(creditService, times(1)).awardRecyclingCredits(eq(item), anyString());
    }

    @Test
    @DisplayName("flagHazardousMaterial should attach toxic substance and record audit trail")
    void testFlagHazardousMaterial_Success() {
        when(staffRepository.findByEmail("staff@greentech.org")).thenReturn(Optional.of(staff));
        when(ewasteItemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(ewasteItemRepository.save(any(EWasteItem.class))).thenAnswer(i -> i.getArgument(0));
        when(hazardousMaterialRepository.save(any(HazardousMaterial.class))).thenAnswer(i -> {
            HazardousMaterial m = i.getArgument(0);
            m.setId(88L);
            return m;
        });

        FlagHazardousMaterialRequest request = new FlagHazardousMaterialRequest(HazardousType.BATTERY);
        HazardousMaterialResponse response = facilityService.flagHazardousMaterial("staff@greentech.org", 100L, request);

        assertNotNull(response);
        assertEquals(88L, response.getId());
        assertEquals(HazardousType.BATTERY, response.getType());
        assertEquals(DiversionStatus.FLAGGED, response.getDiversionStatus());

        verify(trackingService, times(1)).recordTracking(
                eq(EntityType.EWASTE_ITEM),
                eq(100L),
                anyString(),
                eq(staff),
                contains("BATTERY")
        );
    }
}
