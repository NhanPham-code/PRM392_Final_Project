package com.example.bakeryshop.Data.DTO;
import com.google.gson.annotations.SerializedName;

public class LoginResponseDTO {
    @SerializedName("token")
    private String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
