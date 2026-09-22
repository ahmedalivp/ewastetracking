package com.ewaste.tracking.controller;

import com.ewaste.tracking.dto.consumer.CreditResponse;
import com.ewaste.tracking.dto.consumer.EWasteItemResponse;
import com.ewaste.tracking.dto.consumer.SubmitEWasteItemRequest;
import com.ewaste.tracking.enums.EWasteStatus;
import com.ewaste.tracking.security.JwtAuthenticationFilter;
import com.ewaste.tracking.service.ConsumerService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ConsumerController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class))
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ConsumerController MockMvc Tests")
class ConsumerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ConsumerService consumerService;

    @Test
    @WithMockUser(username = "alice@example.com", roles = {"CONSUMER"})
    @DisplayName("POST /api/consumer/ewaste-items should return 201 Created when submitting item")
    void testSubmitEWasteItem_Success() throws Exception {
        SubmitEWasteItemRequest request = new SubmitEWasteItemRequest(1L, 2L, "MacBook Pro with cracked screen");

        EWasteItemResponse response = new EWasteItemResponse();
        response.setId(10L);
        response.setConsumerId(1L);
        response.setConsumerName("Alice Green");
        response.setCategoryId(1L);
        response.setCategoryName("Laptop");
        response.setDropOffPointId(2L);
        response.setDropOffPointLabel("Downtown Drop Box");
        response.setDeviceDescription("MacBook Pro with cracked screen");
        response.setStatus(EWasteStatus.SUBMITTED);
        response.setSubmittedAt(LocalDateTime.now());

        when(consumerService.submitEWasteItem(eq("alice@example.com"), any(SubmitEWasteItemRequest.class)))
                .thenReturn(response);

        org.springframework.security.core.Authentication auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "alice@example.com", "pass",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CONSUMER")));

        mockMvc.perform(post("/api/consumer/ewaste-items")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("SUBMITTED"))
                .andExpect(jsonPath("$.categoryName").value("Laptop"));
    }

    @Test
    @DisplayName("GET /api/consumer/credits should return 200 OK with consumer credit list")
    void testGetMyCredits_Success() throws Exception {
        CreditResponse credit = new CreditResponse(1L, 50, "Intake reward", LocalDateTime.now());
        when(consumerService.getMyCredits("alice@example.com")).thenReturn(List.of(credit));

        org.springframework.security.core.Authentication auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "alice@example.com", "pass",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CONSUMER")));

        mockMvc.perform(get("/api/consumer/credits")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(50))
                .andExpect(jsonPath("$[0].reason").value("Intake reward"));
    }
}
