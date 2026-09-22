package com.ewaste.tracking.builder;

import com.ewaste.tracking.entity.Consumer;
import com.ewaste.tracking.entity.DropOffPoint;
import com.ewaste.tracking.entity.EWasteCategory;
import com.ewaste.tracking.entity.EWasteItem;
import com.ewaste.tracking.entity.builder.EWasteItemBuilder;
import com.ewaste.tracking.enums.EWasteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EWasteItemBuilder Unit Tests - Hand-Written Builder Pattern")
class EWasteItemBuilderTest {

    private Consumer sampleConsumer;
    private EWasteCategory sampleCategory;
    private DropOffPoint sampleDropOffPoint;

    @BeforeEach
    void setUp() {
        sampleConsumer = new Consumer("Alice Green", "alice@example.com", "hash123", 100);
        sampleCategory = new EWasteCategory("Laptop", "Portable computers");
        sampleCategory.setId(1L);
        sampleDropOffPoint = new DropOffPoint(null, "Central Drop Box", 37.77, -122.41);
        sampleDropOffPoint.setId(10L);
    }

    @Test
    @DisplayName("Should successfully construct EWasteItem with all attributes using builder")
    void testBuild_FullAttributes_Success() {
        LocalDateTime customTime = LocalDateTime.of(2026, 9, 21, 10, 0);

        EWasteItem item = new EWasteItemBuilder()
                .consumer(sampleConsumer)
                .category(sampleCategory)
                .dropOffPoint(sampleDropOffPoint)
                .deviceDescription("MacBook Pro 16 with damaged display")
                .status(EWasteStatus.RECEIVED)
                .submittedAt(customTime)
                .build();

        assertNotNull(item);
        assertEquals(sampleConsumer, item.getConsumer());
        assertEquals(sampleCategory, item.getCategory());
        assertEquals(sampleDropOffPoint, item.getDropOffPoint());
        assertEquals("MacBook Pro 16 with damaged display", item.getDeviceDescription());
        assertEquals(EWasteStatus.RECEIVED, item.getStatus());
        assertEquals(customTime, item.getSubmittedAt());
    }

    @Test
    @DisplayName("Should apply default status and timestamp when optional fields are omitted")
    void testBuild_DefaultStatusAndTimestamp() {
        EWasteItem item = new EWasteItemBuilder()
                .consumer(sampleConsumer)
                .category(sampleCategory)
                .deviceDescription("Old Sony Walkman")
                .build();

        assertNotNull(item);
        assertNull(item.getDropOffPoint(), "Drop-off point should be null when omitted");
        assertEquals(EWasteStatus.SUBMITTED, item.getStatus(), "Default status must be SUBMITTED");
        assertNotNull(item.getSubmittedAt(), "Default submittedAt must not be null");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when consumer is missing")
    void testBuild_MissingConsumer_ThrowsException() {
        EWasteItemBuilder builder = new EWasteItemBuilder()
                .category(sampleCategory)
                .deviceDescription("Broken Tablet");

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("consumer must not be null"));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when category is missing")
    void testBuild_MissingCategory_ThrowsException() {
        EWasteItemBuilder builder = new EWasteItemBuilder()
                .consumer(sampleConsumer)
                .deviceDescription("Broken Tablet");

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("category must not be null"));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when deviceDescription is blank")
    void testBuild_BlankDeviceDescription_ThrowsException() {
        EWasteItemBuilder builder = new EWasteItemBuilder()
                .consumer(sampleConsumer)
                .category(sampleCategory)
                .deviceDescription("   ");

        IllegalStateException ex = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(ex.getMessage().contains("deviceDescription must not be blank"));
    }
}
