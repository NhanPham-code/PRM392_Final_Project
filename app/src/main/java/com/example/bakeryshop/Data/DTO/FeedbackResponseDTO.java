package com.example.bakeryshop.Data.DTO;

import com.google.gson.annotations.SerializedName;

public class FeedbackResponseDTO {

    @SerializedName("feedbackId")
    private int feedbackId;

    @SerializedName("userId")
    private int userId;

    @SerializedName("description")
    private String description;

    @SerializedName("submittedDate")
    private String submittedDate;

    public FeedbackResponseDTO() {
    }

    public FeedbackResponseDTO(int feedbackId, int userId, String description, String submittedDate) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.description = description;
        this.submittedDate = submittedDate;
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(String submittedDate) {
        this.submittedDate = submittedDate;
    }
}
