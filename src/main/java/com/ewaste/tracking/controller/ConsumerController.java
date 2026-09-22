package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.consumer.*;
import com.ewaste.tracking.service.ConsumerService;
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
@RequestMapping("/api/consumer")
@PreAuthorize("hasRole('CONSUMER')")
@Tag(name = "Consumer Portal", description = "Endpoints for consumers to register e-waste, view audit timelines, and redeem credits")
@SecurityRequirement(name = "BearerAuth")
public class ConsumerController {

    private final ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @PostMapping("/ewaste-items")
    @Operation(summary = "Submit a discarded e-waste device", description = "Constructs an EWasteItem using EWasteItemBuilder and logs an initial TrackingRecord.")
    public ResponseEntity<EWasteItemResponse> submitEWasteItem(
            Authentication authentication,
            @Valid @RequestBody SubmitEWasteItemRequest request) {
        EWasteItemResponse response = consumerService.submitEWasteItem(getEmail(authentication), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/ewaste-items")
    @Operation(summary = "List consumer's submitted items", description = "Retrieves all e-waste items submitted by the authenticated consumer.")
    public ResponseEntity<List<EWasteItemResponse>> getMySubmissions(Authentication authentication) {
        List<EWasteItemResponse> submissions = consumerService.getMySubmissions(getEmail(authentication));
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/ewaste-items/{id}/tracking")
    @Operation(summary = "Get lifecycle tracking timeline", description = "Retrieves the immutable audit trail of status changes for the specified e-waste item.")
    public ResponseEntity<List<TrackingRecordResponse>> getItemTracking(
            Authentication authentication,
            @PathVariable Long id) {
        List<TrackingRecordResponse> timeline = consumerService.getItemTrackingTimeline(getEmail(authentication), id);
        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/credits")
    @Operation(summary = "Get earned recycling credits", description = "Retrieves the history of credits awarded for recycling participation.")
    public ResponseEntity<List<CreditResponse>> getMyCredits(Authentication authentication) {
        List<CreditResponse> credits = consumerService.getMyCredits(getEmail(authentication));
        return ResponseEntity.ok(credits);
    }

    @PostMapping("/credits/redeem")
    @Operation(summary = "Redeem recycling credits", description = "Deducts earned points in exchange for eco-rewards or partner vouchers.")
    public ResponseEntity<CreditRedemptionResponse> redeemCredits(
            Authentication authentication,
            @Valid @RequestBody RedeemCreditRequest request) {
        CreditRedemptionResponse response = consumerService.redeemCredits(getEmail(authentication), request);
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
