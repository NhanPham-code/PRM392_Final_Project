package com.example.bakeryshop.Data.DTO;
public class FeedbackRequestDTO {
    private String description;

    public FeedbackRequestDTO( String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}