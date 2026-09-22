package com.ewaste.app;

import com.ewaste.app.data.model.ComponentResponse;
import com.ewaste.app.data.model.FlagHazardousMaterialRequest;
import com.ewaste.app.data.model.HarvestComponentRequest;
import com.ewaste.app.data.model.UpdateCategorizeRequest;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class FacilityDtoTest {

    private final Gson gson = new Gson();

    @Test
    public void testHarvestComponentRequestSerialization() {
        HarvestComponentRequest request = new HarvestComponentRequest("16GB DDR4 RAM", "RAM", "GOOD");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"name\":\"16GB DDR4 RAM\""));
        assertTrue(json.contains("\"type\":\"RAM\""));
        assertTrue(json.contains("\"condition\":\"GOOD\""));
    }

    @Test
    public void testUpdateCategorizeRequestSerialization() {
        UpdateCategorizeRequest request = new UpdateCategorizeRequest(2L, "Inspected battery health and screen integrity");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"categoryId\":2"));
        assertTrue(json.contains("\"triageNotes\":\"Inspected battery health and screen integrity\""));
    }

    @Test
    public void testFlagHazardousMaterialRequestSerialization() {
        FlagHazardousMaterialRequest request = new FlagHazardousMaterialRequest("BATTERY");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"type\":\"BATTERY\""));
    }

    @Test
    public void testComponentResponseDeserialization() {
        String json = "{\"id\":55,\"ewasteItemId\":10,\"categoryName\":\"Laptop\",\"name\":\"Samsung 512GB NVMe SSD\",\"type\":\"STORAGE\",\"condition\":\"GOOD\",\"status\":\"AVAILABLE\",\"harvestedAt\":\"2026-09-21T12:00:00\"}";
        ComponentResponse response = gson.fromJson(json, ComponentResponse.class);

        assertNotNull(response);
        assertEquals(Long.valueOf(55), response.getId());
        assertEquals(Long.valueOf(10), response.getEwasteItemId());
        assertEquals("Laptop", response.getCategoryName());
        assertEquals("Samsung 512GB NVMe SSD", response.getName());
        assertEquals("STORAGE", response.getType());
        assertEquals("GOOD", response.getCondition());
        assertEquals("AVAILABLE", response.getStatus());
    }
}
