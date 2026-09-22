package com.ewaste.tracking.service;

import com.ewaste.tracking.dto.auth.AuthResponse;
import com.ewaste.tracking.dto.auth.LoginRequest;
import com.ewaste.tracking.dto.auth.RegisterRequest;
import com.ewaste.tracking.dto.auth.UserProfileResponse;

/**
 * OOP PRINCIPLE: ABSTRACTION
 * 
 * Defines identity and security lifecycle contracts including user registration
 * (instantiating appropriate User subclasses), authentication, and profile querying.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getProfile(String email);
}
