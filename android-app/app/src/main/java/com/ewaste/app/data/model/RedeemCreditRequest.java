package com.ewaste.app.data.model;

import com.google.gson.annotations.SerializedName;

public class RedeemCreditRequest {
    @SerializedName("amount")
    private int amount;

    @SerializedName("redeemedFor")
    private String redeemedFor;

    public RedeemCreditRequest(int amount, String redeemedFor) {
        this.amount = amount;
        this.redeemedFor = redeemedFor;
    }

    public int getAmount() { return amount; }
    public String getRedeemedFor() { return redeemedFor; }
}
