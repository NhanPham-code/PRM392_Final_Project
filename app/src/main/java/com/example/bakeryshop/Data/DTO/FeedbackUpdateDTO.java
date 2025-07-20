package com.example.bakeryshop.Data.DTO;

public class FeedbackUpdateDTO {
    private int feedbackID;
    private String description;

    public FeedbackUpdateDTO(int feedbackID, String description) {
        this.feedbackID = feedbackID;
        this.description = description;
    }

    public FeedbackUpdateDTO() {
    }

    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}