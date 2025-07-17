package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

public class ReadOrderDTO {
    @SerializedName("orderID")
    private int orderID;

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }
}
