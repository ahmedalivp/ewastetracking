package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.admin.FacilityResponse;
import com.ewaste.tracking.dto.admin.VerifyFacilityRequest;
import com.ewaste.tracking.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administrator Oversight", description = "Endpoints for platform administrators to audit and verify recycling facilities")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/facilities")
    @Operation(summary = "List all recycling facilities", description = "Returns all registered recycling facilities with certification and verification status.")
    public ResponseEntity<List<FacilityResponse>> getAllFacilities() {
        List<FacilityResponse> facilities = adminService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    @PutMapping("/facilities/{id}/verify")
    @Operation(summary = "Verify or unverify a recycling facility", description = "Toggles regulatory admin verification for a recycling facility.")
    public ResponseEntity<FacilityResponse> verifyFacility(
            @PathVariable Long id,
            @Valid @RequestBody VerifyFacilityRequest request) {
        FacilityResponse response = adminService.verifyFacility(id, request.getVerified());
        return ResponseEntity.ok(response);
    }
}
