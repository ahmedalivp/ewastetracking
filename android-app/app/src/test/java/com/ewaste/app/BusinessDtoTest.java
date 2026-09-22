package com.ewaste.app;

import com.ewaste.app.data.model.ComponentRequestResponse;
import com.ewaste.app.data.model.CreateComponentRequestDto;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class BusinessDtoTest {

    private final Gson gson = new Gson();

    @Test
    public void testCreateComponentRequestDtoSerialization() {
        CreateComponentRequestDto request = new CreateComponentRequestDto(42L);
        String json = gson.toJson(request);

        assertTrue(json.contains("\"componentId\":42"));
    }

    @Test
    public void testComponentRequestResponseDeserialization() {
        String json = "{\"id\":10,\"businessId\":3,\"businessName\":\"Circular Electronics\",\"componentId\":42,\"componentName\":\"Intel Core i7 CPU\",\"componentType\":\"CPU\",\"componentCondition\":\"GOOD\",\"status\":\"APPROVED\",\"requestedAt\":\"2026-09-21T14:00:00\"}";
        ComponentRequestResponse response = gson.fromJson(json, ComponentRequestResponse.class);

        assertNotNull(response);
        assertEquals(Long.valueOf(10), response.getId());
        assertEquals(Long.valueOf(3), response.getBusinessId());
        assertEquals("Circular Electronics", response.getBusinessName());
        assertEquals(Long.valueOf(42), response.getComponentId());
        assertEquals("Intel Core i7 CPU", response.getComponentName());
        assertEquals("CPU", response.getComponentType());
        assertEquals("GOOD", response.getComponentCondition());
        assertEquals("APPROVED", response.getStatus());
        assertEquals("2026-09-21T14:00:00", response.getRequestedAt());
    }
}
