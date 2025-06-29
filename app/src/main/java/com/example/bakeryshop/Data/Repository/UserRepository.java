package com.example.bakeryshop.Data.Repository;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.LoginRequestDTO;
import com.example.bakeryshop.Data.DTO.LoginResponseDTO;
import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.Data.DTO.RegisterRequestDTO;

import retrofit2.Call;

public class UserRepository {
    private final ApiService apiService;

    public UserRepository() {
        this.apiService = ApiClient.getInstance().getApiService();
    }

    public Call<LoginResponseDTO> login(LoginRequestDTO request) {
        return apiService.login(request);
    }

    public Call<ReadUserDTO> register(RegisterRequestDTO request) {
        return apiService.register(request);
    }
}
