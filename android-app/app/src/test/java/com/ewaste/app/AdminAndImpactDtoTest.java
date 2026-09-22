package com.ewaste.app;

import com.ewaste.app.data.local.db.CachedDropOffPoint;
import com.ewaste.app.data.local.db.CachedSubmission;
import com.ewaste.app.data.model.FacilityResponse;
import com.ewaste.app.data.model.ImpactStatsResponse;
import com.ewaste.app.data.model.VerifyFacilityRequest;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdminAndImpactDtoTest {

    private final Gson gson = new Gson();

    @Test
    public void testImpactStatsResponseDeserialization() {
        String json = "{\"totalSubmissions\":12,\"totalComponentsHarvested\":8,\"totalHazardousDiverted\":3," +
                "\"totalCreditsAwarded\":350,\"divertedLandfillKg\":63.6,\"co2EmissionsSavedKg\":178.1,\"activeFacilities\":2}";

        ImpactStatsResponse stats = gson.fromJson(json, ImpactStatsResponse.class);

        assertNotNull(stats);
        assertEquals(12, stats.getTotalSubmissions());
        assertEquals(8, stats.getTotalComponentsHarvested());
        assertEquals(3, stats.getTotalHazardousDiverted());
        assertEquals(350, stats.getTotalCreditsAwarded());
        assertEquals(63.6, stats.getDivertedLandfillKg(), 0.01);
        assertEquals(178.1, stats.getCo2EmissionsSavedKg(), 0.01);
        assertEquals(2, stats.getActiveFacilities());
    }

    @Test
    public void testFacilityResponseDeserialization() {
        String json = "{\"id\":1,\"name\":\"GreenTech Facility\",\"address\":\"100 Circular Way\",\"lat\":37.7749,\"lng\":-122.4194,\"verifiedByAdmin\":true}";

        FacilityResponse facility = gson.fromJson(json, FacilityResponse.class);

        assertNotNull(facility);
        assertEquals(Long.valueOf(1), facility.getId());
        assertEquals("GreenTech Facility", facility.getName());
        assertEquals("100 Circular Way", facility.getAddress());
        assertEquals(37.7749, facility.getLat(), 0.0001);
        assertEquals(-122.4194, facility.getLng(), 0.0001);
        assertTrue(facility.isVerifiedByAdmin());
    }

    @Test
    public void testVerifyFacilityRequestSerialization() {
        VerifyFacilityRequest req = new VerifyFacilityRequest(true);
        String json = gson.toJson(req);

        assertTrue(json.contains("\"verified\":true"));
    }

    @Test
    public void testCachedEntitiesMapping() {
        CachedDropOffPoint point = new CachedDropOffPoint(5L, "Downtown Hub", 37.77, -122.41, 1L, "Facility A");
        assertEquals(Long.valueOf(5), point.getId());
        assertEquals("Downtown Hub", point.getLabel());

        CachedSubmission submission = new CachedSubmission(10L, "Laptop", "Dell XPS", "Hub A", "PROCESSED", "2026-09-21", 2, 0);
        assertEquals(Long.valueOf(10), submission.getId());
        assertEquals("PROCESSED", submission.getStatus());
        assertEquals(2, submission.getComponentCount());
    }

    @Test
    public void testQrPassportTokenFormattingAndParsing() {
        String token = com.ewaste.app.util.QrCodeHelper.formatPassportToken(42L);
        assertEquals("EWASTE-0042", token);

        Long parsedId = com.ewaste.app.util.QrCodeHelper.parsePassportToken("EWASTE-0042");
        assertEquals(Long.valueOf(42), parsedId);

        Long numericParsed = com.ewaste.app.util.QrCodeHelper.parsePassportToken("42");
        assertEquals(Long.valueOf(42), numericParsed);

        assertNull(com.ewaste.app.util.QrCodeHelper.parsePassportToken("INVALID_TOKEN"));
        assertNull(com.ewaste.app.util.QrCodeHelper.parsePassportToken(null));
    }
}
