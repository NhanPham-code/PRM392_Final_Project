package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.CreateOrderDTO;
import com.example.bakeryshop.Data.DTO.CreateOrderDetailDTO;
import com.example.bakeryshop.Data.DTO.ReadOrderDTO;

import retrofit2.Call;

public class OrderRepository {
    private final ApiService apiService;

    public OrderRepository(Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();
    }

    public Call<ReadOrderDTO> createOrder(CreateOrderDTO order) {
        return apiService.createOrder(order);
    }

    public Call<Void> createOrderDetail(CreateOrderDetailDTO orderDetail) {
        return apiService.createOrderDetail(orderDetail);
    }
}
