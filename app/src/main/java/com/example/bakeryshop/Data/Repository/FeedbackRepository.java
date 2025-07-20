package com.example.bakeryshop.Data.Repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.FeedbackRequestDTO;
import com.example.bakeryshop.Data.DTO.FeedbackResponseDTO;
import com.example.bakeryshop.Data.DTO.FeedbackUpdateDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;

import java.util.List;

import retrofit2.Call;

public class FeedbackRepository {
    // LiveData để thông báo thay đổi dữ liệu
    public final MutableLiveData<Boolean> feedbackChanged = new MutableLiveData<>();
    private static FeedbackRepository instance;
    private final ApiService apiService;

    public static synchronized FeedbackRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FeedbackRepository(context);
        }
        return instance;
    }
    public FeedbackRepository( Context context) {
        this.apiService = ApiClient.getInstance(context).getApiService();;
    }
    public void notifyChange() {
        feedbackChanged.postValue(true);
    }
    public Call<List<FeedbackResponseDTO>> getAllFeedbacks() {
        return apiService.getAllFeedback();
    }
    public Call<FeedbackResponseDTO> getMyFeedback() {
        return apiService.getMyFeedback();
    }
    public Call<Void> createFeedback(FeedbackRequestDTO feedback) {
        return apiService.createFeedback(feedback);
    }
    public Call<Void> deleteFeedback(int userId) {
        return apiService.deleteFeedback(userId);
    }

    public Call<Void> updateFeedback(int feedbackId, FeedbackUpdateDTO dto) {
        return apiService.updateFeedback(feedbackId, dto);
    }
}
