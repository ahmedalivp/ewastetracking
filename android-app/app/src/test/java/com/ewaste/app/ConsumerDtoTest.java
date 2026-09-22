package com.ewaste.app;

import com.ewaste.app.data.model.CategoryResponse;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.data.model.EWasteItemResponse;
import com.ewaste.app.data.model.SubmitEWasteItemRequest;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConsumerDtoTest {

    private final Gson gson = new Gson();

    @Test
    public void testSubmitEWasteItemRequestSerialization() {
        SubmitEWasteItemRequest request = new SubmitEWasteItemRequest(1L, 2L, "MacBook Pro with broken logic board");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"categoryId\":1"));
        assertTrue(json.contains("\"dropOffPointId\":2"));
        assertTrue(json.contains("\"deviceDescription\":\"MacBook Pro with broken logic board\""));
    }

    @Test
    public void testEWasteItemResponseDeserialization() {
        String json = "{\"id\":101,\"consumerId\":1,\"consumerName\":\"Alice Green\",\"categoryId\":1,\"categoryName\":\"Laptop\",\"dropOffPointId\":1,\"dropOffPointLabel\":\"Central Hub\",\"deviceDescription\":\"Dell XPS 15\",\"status\":\"SUBMITTED\",\"submittedAt\":\"2026-09-21T10:00:00\",\"componentCount\":2,\"hazardousMaterialCount\":1}";
        EWasteItemResponse response = gson.fromJson(json, EWasteItemResponse.class);

        assertNotNull(response);
        assertEquals(Long.valueOf(101), response.getId());
        assertEquals("Laptop", response.getCategoryName());
        assertEquals("SUBMITTED", response.getStatus());
        assertEquals(2, response.getComponentCount());
        assertEquals(1, response.getHazardousMaterialCount());
    }

    @Test
    public void testCategoryResponseDeserialization() {
        String json = "{\"id\":1,\"name\":\"Laptop\",\"description\":\"Portable computers\"}";
        CategoryResponse response = gson.fromJson(json, CategoryResponse.class);

        assertNotNull(response);
        assertEquals(Long.valueOf(1), response.getId());
        assertEquals("Laptop", response.getName());
        assertEquals("Portable computers", response.getDescription());
    }
}
