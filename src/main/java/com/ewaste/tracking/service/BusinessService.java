package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.business.CreateComponentRequestDto;
import com.ewaste.tracking.dto.facility.ComponentResponse;

import java.util.List;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines commercial business interactions: querying salvageable components
 * for circular supply chains and managing component acquisition requisitions.
 */
public interface BusinessService {

    List<ComponentResponse> getAvailableComponents(String categoryName);

    ComponentRequestResponse requestComponent(String businessEmail, CreateComponentRequestDto request);

    List<ComponentRequestResponse> getMyComponentRequests(String businessEmail);
}
