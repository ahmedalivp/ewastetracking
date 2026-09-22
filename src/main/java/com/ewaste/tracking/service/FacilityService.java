package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.consumer.EWasteItemResponse;
import com.ewaste.tracking.dto.facility.*;
import com.ewaste.tracking.enums.EWasteStatus;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines operations performed by recycling facility staff:
 * intake inspection, receiving, triage categorization, component harvesting,
 * hazardous material diversion, and business request fulfillment.
 */
public interface FacilityService {

    List<EWasteItemResponse> getIntakeQueue(EWasteStatus status);

    EWasteItemResponse receiveItem(String staffEmail, Long itemId);

    EWasteItemResponse categorizeItem(String staffEmail, Long itemId, UpdateCategorizeRequest request);

    ComponentResponse harvestComponent(String staffEmail, Long itemId, HarvestComponentRequest request);

    HazardousMaterialResponse flagHazardousMaterial(String staffEmail, Long itemId, FlagHazardousMaterialRequest request);

    HazardousMaterialResponse divertHazardousMaterial(String staffEmail, Long materialId);

    ComponentRequestResponse updateComponentRequestStatus(String staffEmail, Long requestId, UpdateComponentRequestStatusRequest request);
}
