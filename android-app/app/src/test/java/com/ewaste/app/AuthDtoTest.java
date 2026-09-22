package com.ewaste.app;

import com.ewaste.app.data.model.AuthResponse;
import com.ewaste.app.data.model.LoginRequest;
import com.ewaste.app.data.model.RegisterRequest;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class AuthDtoTest {

    private final Gson gson = new Gson();

    @Test
    public void testLoginRequestSerialization() {
        LoginRequest request = new LoginRequest("alice@example.com", "pass123");
        String json = gson.toJson(request);
        assertTrue(json.contains("\"email\":\"alice@example.com\""));
        assertTrue(json.contains("\"password\":\"pass123\""));
    }

    @Test
    public void testRegisterRequestSerialization() {
        RegisterRequest request = new RegisterRequest("Alice Green", "alice@example.com", "pass123", "CONSUMER");
        request.setFacilityId(1L);
        request.setBusinessName("Test Corp");
        request.setBusinessType("Refurbisher");

        String json = gson.toJson(request);
        assertTrue(json.contains("\"fullName\":\"Alice Green\""));
        assertTrue(json.contains("\"role\":\"CONSUMER\""));
        assertTrue(json.contains("\"facilityId\":1"));
        assertTrue(json.contains("\"businessName\":\"Test Corp\""));
    }

    @Test
    public void testAuthResponseDeserialization() {
        String json = "{\"token\":\"jwt-sample-token\",\"type\":\"Bearer\",\"id\":10,\"email\":\"alice@example.com\",\"fullName\":\"Alice Green\",\"role\":\"CONSUMER\",\"dashboardSummary\":\"Welcome back\"}";
        AuthResponse response = gson.fromJson(json, AuthResponse.class);

        assertNotNull(response);
        assertEquals("jwt-sample-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(Long.valueOf(10), response.getId());
        assertEquals("alice@example.com", response.getEmail());
        assertEquals("Alice Green", response.getFullName());
        assertEquals("CONSUMER", response.getRole());
        assertEquals("Welcome back", response.getDashboardSummary());
    }
}
