package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.auth.AuthResponse;
import com.ewaste.tracking.dto.auth.LoginRequest;
import com.ewaste.tracking.dto.auth.RegisterRequest;
import com.ewaste.tracking.dto.auth.UserProfileResponse;
import com.ewaste.tracking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication & Profiles", description = "Endpoints for user registration, JWT login, and profile inspection")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account", description = "Creates a Consumer, FacilityStaff, BusinessUser, or AdminUser depending on role.")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT token", description = "Validates credentials and returns JWT bearer token along with polymorphic user summary.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current authenticated user profile", description = "Returns full profile information including polymorphic summary.")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserProfileResponse response = authService.getProfile(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
