package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class CreditResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("amount")
    private int amount;

    @SerializedName("reason")
    private String reason;

    @SerializedName("awardedAt")
    private String awardedAt;

    public Long getId() { return id; }
    public int getAmount() { return amount; }
    public String getReason() { return reason; }
    public String getAwardedAt() { return awardedAt; }
}
