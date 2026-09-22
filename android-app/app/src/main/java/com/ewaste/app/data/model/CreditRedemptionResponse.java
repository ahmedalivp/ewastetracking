package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class CreditRedemptionResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("amount")
    private int amount;

    @SerializedName("redeemedFor")
    private String redeemedFor;

    @SerializedName("redeemedAt")
    private String redeemedAt;

    @SerializedName("remainingBalance")
    private int remainingBalance;

    public Long getId() { return id; }
    public int getAmount() { return amount; }
    public String getRedeemedFor() { return redeemedFor; }
    public String getRedeemedAt() { return redeemedAt; }
    public int getRemainingBalance() { return remainingBalance; }
}
