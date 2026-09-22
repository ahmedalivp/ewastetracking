package com.ewaste.tracking.dto.common;

public class ImpactStatsResponse {

    private long totalSubmissions;
    private long totalComponentsHarvested;
    private long totalHazardousDiverted;
    private long totalCreditsAwarded;
    private double divertedLandfillKg;
    private double co2EmissionsSavedKg;
    private long activeFacilities;

    public ImpactStatsResponse() {}

    public ImpactStatsResponse(long totalSubmissions, long totalComponentsHarvested, long totalHazardousDiverted,
                               long totalCreditsAwarded, double divertedLandfillKg, double co2EmissionsSavedKg,
                               long activeFacilities) {
        this.totalSubmissions = totalSubmissions;
        this.totalComponentsHarvested = totalComponentsHarvested;
        this.totalHazardousDiverted = totalHazardousDiverted;
        this.totalCreditsAwarded = totalCreditsAwarded;
        this.divertedLandfillKg = divertedLandfillKg;
        this.co2EmissionsSavedKg = co2EmissionsSavedKg;
        this.activeFacilities = activeFacilities;
    }

    public long getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(long totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public long getTotalComponentsHarvested() {
        return totalComponentsHarvested;
    }

    public void setTotalComponentsHarvested(long totalComponentsHarvested) {
        this.totalComponentsHarvested = totalComponentsHarvested;
    }

    public long getTotalHazardousDiverted() {
        return totalHazardousDiverted;
    }

    public void setTotalHazardousDiverted(long totalHazardousDiverted) {
        this.totalHazardousDiverted = totalHazardousDiverted;
    }

    public long getTotalCreditsAwarded() {
        return totalCreditsAwarded;
    }

    public void setTotalCreditsAwarded(long totalCreditsAwarded) {
        this.totalCreditsAwarded = totalCreditsAwarded;
    }

    public double getDivertedLandfillKg() {
        return divertedLandfillKg;
    }

    public void setDivertedLandfillKg(double divertedLandfillKg) {
        this.divertedLandfillKg = divertedLandfillKg;
    }

    public double getCo2EmissionsSavedKg() {
        return co2EmissionsSavedKg;
    }

    public void setCo2EmissionsSavedKg(double co2EmissionsSavedKg) {
        this.co2EmissionsSavedKg = co2EmissionsSavedKg;
    }

    public long getActiveFacilities() {
        return activeFacilities;
    }

    public void setActiveFacilities(long activeFacilities) {
        this.activeFacilities = activeFacilities;
    }
}
