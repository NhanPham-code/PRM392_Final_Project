package com.example.bakeryshop.Data.Api;

import com.example.bakeryshop.Data.DTO.LoginRequestDTO;
import com.example.bakeryshop.Data.DTO.LoginResponseDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.Data.DTO.RegisterRequestDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);

    @POST("users/register")
    Call<ReadUserDTO> register(@Body RegisterRequestDTO user);

    @GET("products")
    Call<List<ReadProductDTO>> getProducts();

    @GET("products/category/{id}")
    Call<List<ReadProductDTO>> getProductsByCategoryId(@Path("id") int categoryId);

    @GET("products/search")
    Call<List<ReadProductDTO>> searchProducts(@Query("searchKey") String searchKey);

    @GET("products/{id}")
    Call<ReadProductDTO> getProductById(@Path("id") int productId);
}
