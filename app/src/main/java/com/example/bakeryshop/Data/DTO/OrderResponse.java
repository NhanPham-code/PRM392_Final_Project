package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {
        @SerializedName("value")
    private List<ReadOrderHistoryDTO> value;

    public List<ReadOrderHistoryDTO> getValue() {
        return value;
    }

    public void setValue(List<ReadOrderHistoryDTO> value) {
        this.value = value;
    }
}
