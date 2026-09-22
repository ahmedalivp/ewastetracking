package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.auth.AuthResponse;
import com.ewaste.tracking.dto.auth.LoginRequest;
import com.ewaste.tracking.dto.auth.RegisterRequest;
import com.ewaste.tracking.enums.Role;
import com.ewaste.tracking.exception.GlobalExceptionHandler;
import com.ewaste.tracking.exception.UnauthorizedException;
import com.ewaste.tracking.security.JwtAuthenticationFilter;
import com.ewaste.tracking.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController MockMvc Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register should return 201 Created with JWT token and user summary")
    void testRegister_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("David Doe");
        request.setEmail("david@example.com");
        request.setPassword("securePassword123");
        request.setRole(Role.CONSUMER);

        AuthResponse mockResponse = new AuthResponse("mock.jwt.token", 1L, "david@example.com", "David Doe", Role.CONSUMER, "Consumer [David Doe] - Available Recycling Credits: 0 pts");
        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.email").value("david@example.com"))
                .andExpect(jsonPath("$.role").value("CONSUMER"))
                .andExpect(jsonPath("$.dashboardSummary").exists());
    }

    @Test
    @DisplayName("POST /api/auth/login should return 200 OK with valid credentials")
    void testLogin_Success() throws Exception {
        LoginRequest request = new LoginRequest("david@example.com", "securePassword123");
        AuthResponse mockResponse = new AuthResponse("mock.jwt.token", 1L, "david@example.com", "David Doe", Role.CONSUMER, "Consumer [David Doe] - Available Recycling Credits: 50 pts");
        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.email").value("david@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login should return 401 Unauthorized for invalid credentials")
    void testLogin_BadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("david@example.com", "wrongPassword");
        when(authService.login(any(LoginRequest.class))).thenThrow(new UnauthorizedException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/auth/register should return 400 Bad Request on Bean Validation failure")
    void testRegister_ValidationFailure() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFullName(""); // Blank
        request.setEmail("not-an-email"); // Invalid email
        request.setPassword("123"); // Too short
        request.setRole(null); // Missing role

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.validationErrors").isMap());
    }
}
