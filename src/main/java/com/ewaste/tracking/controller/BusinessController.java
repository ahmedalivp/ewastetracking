package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.business.ComponentRequestResponse;
import com.ewaste.tracking.dto.business.CreateComponentRequestDto;
import com.ewaste.tracking.dto.facility.ComponentResponse;
import com.ewaste.tracking.service.BusinessService;
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
@RequestMapping("/api/business")
@PreAuthorize("hasAnyRole('BUSINESS', 'FACILITY_STAFF')")
@Tag(name = "Business Marketplace", description = "Endpoints for circular enterprises, repair shops, and refurbishers to acquire harvested parts")
@SecurityRequirement(name = "BearerAuth")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @GetMapping("/components")
    @Operation(summary = "Search available salvaged components", description = "Queries secondary market inventory for available parts, optionally filtered by original device category.")
    public ResponseEntity<List<ComponentResponse>> getAvailableComponents(
            @RequestParam(required = false) String category) {
        List<ComponentResponse> components = businessService.getAvailableComponents(category);
        return ResponseEntity.ok(components);
    }

    @PostMapping("/component-requests")
    @PreAuthorize("hasRole('BUSINESS')")
    @Operation(summary = "Request a component for reuse", description = "Submits an acquisition requisition for an available salvaged hardware component.")
    public ResponseEntity<ComponentRequestResponse> requestComponent(
            Authentication authentication,
            @Valid @RequestBody CreateComponentRequestDto request) {
        ComponentRequestResponse response = businessService.requestComponent(getEmail(authentication), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/component-requests")
    @PreAuthorize("hasRole('BUSINESS')")
    @Operation(summary = "List business component requests", description = "Retrieves all component requisitions submitted by the authenticated business user.")
    public ResponseEntity<List<ComponentRequestResponse>> getMyComponentRequests(Authentication authentication) {
        List<ComponentRequestResponse> requests = businessService.getMyComponentRequests(getEmail(authentication));
        return ResponseEntity.ok(requests);
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
