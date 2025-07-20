package com.example.bakeryshop.Data.Repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.OrderResponse;
import com.example.bakeryshop.Data.DTO.ReadOrderDTO;

import java.util.List;

import retrofit2.Call;

public class OrderRepository  {
    private final ApiService apiService;

    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private SharedPreferences sharedPreferences;

    private String token = "";


    public OrderRepository(Context context){
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        token = sharedPreferences.getString(KEY_TOKEN, null);
        this.apiService = ApiClient.getInstance(context).getApiService();
    }

    public Call<OrderResponse> getAllOrderByUser(){
        return apiService.getAllOrderByUserToken("Bearer " + token);
    }

}
