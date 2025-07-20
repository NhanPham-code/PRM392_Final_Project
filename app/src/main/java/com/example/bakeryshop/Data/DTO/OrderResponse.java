package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {
        @SerializedName("value")
    private List<ReadOrderDTO> value;

    public List<ReadOrderDTO> getValue() {
        return value;
    }

    public void setValue(List<ReadOrderDTO> value) {
        this.value = value;
    }
}
