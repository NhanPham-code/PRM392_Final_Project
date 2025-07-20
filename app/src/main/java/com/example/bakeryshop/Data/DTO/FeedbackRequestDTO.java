package com.example.bakeryshop.Data.DTO;
public class FeedbackRequestDTO {
    private int userId;
    private String description;

    public FeedbackRequestDTO(int userId, String description) {
        this.userId = userId;
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

}