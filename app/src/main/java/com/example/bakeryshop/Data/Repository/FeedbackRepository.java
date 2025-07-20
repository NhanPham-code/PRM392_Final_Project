package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.FeedbackRequestDTO;
import com.example.bakeryshop.Data.DTO.FeedbackResponseDTO;
import com.example.bakeryshop.Data.DTO.FeedbackUpdateDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;

import java.util.List;

import retrofit2.Call;

public class FeedbackRepository {

    private final ApiService apiService;


    public FeedbackRepository( Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();;
    }

    public Call<List<FeedbackResponseDTO>> getAllFeedbacks() {
        return apiService.getAllFeedback();
    }

    public Call<Void> createFeedback(FeedbackRequestDTO feedback) {
        return apiService.createFeedback(feedback);
    }
    public Call<Void> deleteFeedback(int feedbackId) {
        return apiService.deleteFeedback(feedbackId);
    }

    public Call<Void> updateFeedback(int feedbackId, String newDescription) {
        FeedbackUpdateDTO dto = new FeedbackUpdateDTO(feedbackId, newDescription);
        return apiService.updateFeedback(feedbackId, dto);
    }
}
