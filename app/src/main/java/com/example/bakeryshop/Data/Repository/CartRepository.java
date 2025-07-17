package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.AddCartItemRequestDTO;
import com.example.bakeryshop.Data.DTO.CartItemDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.DTO.UpdateCartQuantityRequest; // MỚI: Import DTO này

import java.util.List;

import retrofit2.Call;

public class CartRepository {
    private final ApiService apiService;

    public CartRepository(Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();
    }

    public Call<List<CartItemDTO>> getCartUser() {
        return apiService.getCartByToken();
    }

    public Call<Void> deleteCartItem(int cartId) {
        return apiService.deleteCartItem(cartId);
    }

    public Call<Void> updateCartQuantities(List<UpdateCartQuantityRequest> updates) {
        return  apiService.updateCartQuantities(updates);
    }

    public Call<Void> addItemToCart(AddCartItemRequestDTO request) {
        return apiService.addItemToCart(request);
    }
}
