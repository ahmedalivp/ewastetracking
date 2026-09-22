package com.ewaste.tracking.service.impl;

import com.ewaste.tracking.dto.auth.AuthResponse;
import com.ewaste.tracking.dto.auth.LoginRequest;
import com.ewaste.tracking.dto.auth.RegisterRequest;
import com.ewaste.tracking.dto.auth.UserProfileResponse;
import com.ewaste.tracking.entity.*;
import com.ewaste.tracking.enums.Role;
import com.ewaste.tracking.exception.BadRequestException;
import com.ewaste.tracking.exception.ResourceNotFoundException;
import com.ewaste.tracking.exception.UnauthorizedException;
import com.ewaste.tracking.repository.RecyclingFacilityRepository;
import com.ewaste.tracking.repository.UserRepository;
import com.ewaste.tracking.security.JwtTokenProvider;
import com.ewaste.tracking.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OOP PRINCIPLE: ABSTRACTION, INHERITANCE & POLYMORPHISM
 * 
 * - Abstraction: Concrete implementation of the AuthService interface.
 * - Inheritance: Polymorphically instantiates and persists specific User subclasses
 *   (Consumer, FacilityStaff, BusinessUser, AdminUser) according to the requested role.
 * - Polymorphism: Invokes user.getDashboardSummary() uniformly across any User subtype.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RecyclingFacilityRepository recyclingFacilityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           RecyclingFacilityRepository recyclingFacilityRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.recyclingFacilityRepository = recyclingFacilityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String email = request.getEmail().toLowerCase().trim();
        String fullName = request.getFullName().trim();

        User user;
        Role role = request.getRole() != null ? request.getRole() : Role.CONSUMER;

        // Polymorphic instantiation based on role
        switch (role) {
            case FACILITY_STAFF:
                if (request.getFacilityId() == null) {
                    throw new BadRequestException("Facility ID is required when registering Facility Staff");
                }
                RecyclingFacility facility = recyclingFacilityRepository.findById(request.getFacilityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Recycling Facility not found with ID: " + request.getFacilityId()));
                user = new FacilityStaff(fullName, email, encodedPassword, facility);
                break;

            case BUSINESS:
                String bizName = (request.getBusinessName() != null && !request.getBusinessName().isBlank())
                        ? request.getBusinessName().trim() : "Default Business";
                String bizType = (request.getBusinessType() != null && !request.getBusinessType().isBlank())
                        ? request.getBusinessType().trim() : "Refurbisher";
                user = new BusinessUser(fullName, email, encodedPassword, bizName, bizType);
                break;

            case ADMIN:
                String dept = (request.getDepartment() != null && !request.getDepartment().isBlank())
                        ? request.getDepartment().trim() : "Platform Operations";
                user = new AdminUser(fullName, email, encodedPassword, dept);
                break;

            case CONSUMER:
            default:
                user = new Consumer(fullName, email, encodedPassword);
                break;
        }

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateTokenForUser(savedUser.getId(), savedUser.getEmail(), savedUser.getRole().name());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole(),
                savedUser.getDashboardSummary() // Polymorphic method call
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateTokenForUser(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getDashboardSummary() // Polymorphic method call
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setDashboardSummary(user.getDashboardSummary()); // Polymorphic call

        if (user instanceof Consumer consumer) {
            response.setCreditBalance(consumer.getCreditBalance());
        } else if (user instanceof FacilityStaff staff) {
            if (staff.getFacility() != null) {
                response.setFacilityId(staff.getFacility().getId());
                response.setFacilityName(staff.getFacility().getName());
            }
        } else if (user instanceof BusinessUser business) {
            response.setBusinessName(business.getBusinessName());
            response.setBusinessType(business.getBusinessType());
        } else if (user instanceof AdminUser admin) {
            response.setDepartment(admin.getDepartment());
        }

        return response;
    }
}
