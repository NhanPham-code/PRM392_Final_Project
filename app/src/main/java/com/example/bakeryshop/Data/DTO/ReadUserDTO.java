package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ReadUserDTO {

    @SerializedName("UserId")
    private int userId;

    @SerializedName("FullName")
    private String fullName;

    @SerializedName("Email")
    private String email;

    @SerializedName("Role")
    private String role;

    @SerializedName("Address")
    private String address;

    @SerializedName("PhoneNumber")
    private String phoneNumber;

    @SerializedName("RegistrationDate")
    private Date registrationDate;

    public ReadUserDTO() {
    }

    public ReadUserDTO(int userId, String fullName, String email, String role, String address, String phoneNumber, Date registrationDate) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
