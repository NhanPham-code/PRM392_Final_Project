package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.LoginRequestDTO;
import com.example.bakeryshop.Data.DTO.LoginResponseDTO;
import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.Data.DTO.RegisterRequestDTO;
import com.example.bakeryshop.Data.DTO.UpdateUserProfileDTO;

import retrofit2.Call;

public class UserRepository {
    private final ApiService apiService;

    public UserRepository(Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();
    }

    public Call<LoginResponseDTO> login(LoginRequestDTO request) {
        return apiService.login(request);
    }

    public Call<ReadUserDTO> register(RegisterRequestDTO request) {
        return apiService.register(request);
    }

    public Call<ReadUserDTO> getUserInfo() {
        return apiService.getUserInfo();
    }

    public Call<Void> updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO) {
        return apiService.updateUserProfile(updateUserProfileDTO);
    }
}
