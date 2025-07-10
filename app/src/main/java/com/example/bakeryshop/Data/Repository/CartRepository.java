package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.CartItemDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;

import java.util.List;

import retrofit2.Call;
public class CartRepository {
    private final ApiService apiService;


    public CartRepository(Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();
    }

    public Call<List<CartItemDTO>> getCartByUser() {
        return apiService.getCartByToken(); // Gọi /cart/me
    }

    public Call<ReadProductDTO> getProductById(int productId) {
        return apiService.getProductById(productId);
    }
}
