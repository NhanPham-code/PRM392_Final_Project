package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;

import java.util.List;

import retrofit2.Call;

public class ProductRepository {
    private final ApiService apiService;

    public ProductRepository(Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();
    }

    public Call<List<ReadProductDTO>> getProducts() {
        return apiService.getProducts();
    }

    public Call<List<ReadProductDTO>> getProductsByCategoryId(int categoryId) {
        return apiService.getProductsByCategoryId(categoryId);
    }

    public Call<List<ReadProductDTO>> searchProducts(String searchKey) {
        return apiService.searchProducts(searchKey);
    }

    public Call<ReadProductDTO> getProductById(int productId) {
        return apiService.getProductById(productId);
    }
}

