package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.consumer.EWasteItemResponse;
import com.ewaste.tracking.dto.facility.*;
import com.ewaste.tracking.enums.EWasteStatus;
import com.ewaste.tracking.service.BusinessService;
import com.ewaste.tracking.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facility")
@PreAuthorize("hasRole('FACILITY_STAFF')")
@Tag(name = "Recycling Facility Operations", description = "Endpoints for facility staff intake, triage categorization, component harvesting, and hazardous material diversion")
@SecurityRequirement(name = "BearerAuth")
public class FacilityController {

    private final FacilityService facilityService;
    private final BusinessService businessService;

    public FacilityController(FacilityService facilityService, BusinessService businessService) {
        this.facilityService = facilityService;
        this.businessService = businessService;
    }

    @GetMapping("/components")
    @Operation(summary = "View facility inventory of salvaged components", description = "Lists harvested components available in inventory.")
    public ResponseEntity<List<ComponentResponse>> getInventoryComponents(
            @RequestParam(required = false) String category) {
        List<ComponentResponse> components = businessService.getAvailableComponents(category);
        return ResponseEntity.ok(components);
    }

    @GetMapping("/ewaste-items")
    @Operation(summary = "View facility intake queue", description = "Lists e-waste items waiting for intake, optionally filtered by lifecycle status.")
    public ResponseEntity<List<EWasteItemResponse>> getIntakeQueue(
            @RequestParam(required = false) EWasteStatus status) {
        List<EWasteItemResponse> queue = facilityService.getIntakeQueue(status);
        return ResponseEntity.ok(queue);
    }

    @PutMapping("/ewaste-items/{id}/receive")
    @Operation(summary = "Mark e-waste item as received", description = "Updates item status to RECEIVED and records a TrackingRecord audit log.")
    public ResponseEntity<EWasteItemResponse> receiveItem(
            Authentication authentication,
            @PathVariable Long id) {
        EWasteItemResponse response = facilityService.receiveItem(getEmail(authentication), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/ewaste-items/{id}/categorize")
    @Operation(summary = "Categorize and triage item", description = "Assigns verified classification category and triage notes, transitioning status to CATEGORIZED.")
    public ResponseEntity<EWasteItemResponse> categorizeItem(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategorizeRequest request) {
        EWasteItemResponse response = facilityService.categorizeItem(getEmail(authentication), id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ewaste-items/{id}/components")
    @Operation(summary = "Harvest component from e-waste item", description = "Salvages a reusable hardware component, auto-awards recycling credits using the Strategy pattern, and records an immutable TrackingRecord.")
    public ResponseEntity<ComponentResponse> harvestComponent(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody HarvestComponentRequest request) {
        ComponentResponse response = facilityService.harvestComponent(getEmail(authentication), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/ewaste-items/{id}/hazardous-materials")
    @Operation(summary = "Flag hazardous material in item", description = "Records toxic substance presence (e.g. mercury, lead, batteries) for regulatory safety tracking.")
    public ResponseEntity<HazardousMaterialResponse> flagHazardousMaterial(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody FlagHazardousMaterialRequest request) {
        HazardousMaterialResponse response = facilityService.flagHazardousMaterial(getEmail(authentication), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/hazardous-materials/{id}/divert")
    @Operation(summary = "Divert hazardous material safely", description = "Marks a flagged hazardous material as safely diverted from landfills.")
    public ResponseEntity<HazardousMaterialResponse> divertHazardousMaterial(
            Authentication authentication,
            @PathVariable Long id) {
        HazardousMaterialResponse response = facilityService.divertHazardousMaterial(getEmail(authentication), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/component-requests/{id}/status")
    @Operation(summary = "Update status of business component requisition", description = "Approves, fulfills, or rejects an external circular business request for a salvaged component.")
    public ResponseEntity<ComponentRequestResponse> updateComponentRequestStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdateComponentRequestStatusRequest request) {
        ComponentRequestResponse response = facilityService.updateComponentRequestStatus(getEmail(authentication), id, request);
        return ResponseEntity.ok(response);
    }

    private String getEmail(Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            return auth.getName();
        }
        throw new com.ewaste.tracking.exception.UnauthorizedException("User is not authenticated");
    }
}
