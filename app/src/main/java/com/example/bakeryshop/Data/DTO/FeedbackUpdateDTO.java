package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

public class FeedbackUpdateDTO {
    @SerializedName("feedbackID")
    private int feedbackID;

    @SerializedName("description")
    private String description;

    public FeedbackUpdateDTO() {
    }

    public FeedbackUpdateDTO(int feedbackID,String description) {
        this.description = description;
        this.feedbackID = feedbackID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFeedbackID() {
        return feedbackID;
    }

    public void setFeedbackID(int feedbackID) {
        this.feedbackID = feedbackID;
    }
}