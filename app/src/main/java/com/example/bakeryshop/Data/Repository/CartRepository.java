package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
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

    // READ
    public Call<List<CartItemDTO>> getCartByUser() {
        return apiService.getCartByToken();
    }

    // READ (cho chi tiết sản phẩm trong giỏ hàng)
    public Call<ReadProductDTO> getProductById(int productId) {
        return apiService.getProductById(productId);
    }

    // DELETE
    public Call<Void> deleteCartItem(int cartId) {
        return apiService.deleteCartItem(cartId);
    }

    // UPDATE
    public Call<Void> updateCartQuantities(List<UpdateCartQuantityRequest> updates) {
        return apiService.updateCartQuantities(updates);
    }
}