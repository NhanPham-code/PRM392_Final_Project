package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.content.Context;

import com.example.bakeryshop.Data.DTO.FeedbackRequestDTO;
import com.example.bakeryshop.Data.DTO.FeedbackResponseDTO;
import com.example.bakeryshop.Data.Repository.FeedbackRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackViewModel extends AndroidViewModel {

    private final FeedbackRepository feedbackRepository;

    // Danh sách feedback
    private final MutableLiveData<List<FeedbackResponseDTO>> _feedbacks = new MutableLiveData<>();
    public LiveData<List<FeedbackResponseDTO>> feedbacks = _feedbacks;

    // Trạng thái loading
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    // Thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    // Kết quả thêm feedback
    private final MutableLiveData<Boolean> _createSuccess = new MutableLiveData<>();
    public LiveData<Boolean> createSuccess = _createSuccess;

    public FeedbackViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        feedbackRepository = new FeedbackRepository(context);
    }

    /**
     * Gọi API để lấy tất cả phản hồi
     */
    public void fetchFeedbacks() {
        _isLoading.setValue(true);
        clearErrorMessage();
        Log.d("FeedbackListViewModel", "Đang tải danh sách phản hồi...");

        feedbackRepository.getAllFeedbacks().enqueue(new Callback<List<FeedbackResponseDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<FeedbackResponseDTO>> call, @NonNull Response<List<FeedbackResponseDTO>> response) {
                _isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    _feedbacks.setValue(response.body());
                    Log.d("FeedbackListViewModel", "Tải phản hồi thành công. Số lượng: " + response.body().size());
                } else {
                    String error = "Không thể tải phản hồi.";
                    if (response.errorBody() != null) {
                        try {
                            error = response.errorBody().string();
                        } catch (Exception e) {
                            error = "Lỗi không xác định khi đọc errorBody.";
                        }
                    }
                    _errorMessage.setValue(error);
                    Log.e("FeedbackListViewModel", "Lỗi API: " + error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<FeedbackResponseDTO>> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _errorMessage.setValue("Lỗi kết nối mạng: " + t.getMessage());
                Log.e("FeedbackListViewModel", "Lỗi kết nối: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Xóa lỗi đã hiển thị
     */
    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }
    public void addFeedback(int userId, String description, Runnable onSuccess, Runnable onError) {
        _isLoading.setValue(true);

        FeedbackRequestDTO request = new FeedbackRequestDTO(userId, description);

        Log.d("FeedbackDebug", "Gọi API thêm phản hồi");

        feedbackRepository.createFeedback(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    Log.d("FeedbackDebug", "Thêm phản hồi thành công");
                    onSuccess.run();
                } else {
                    Log.e("FeedbackDebug", "Lỗi API thêm phản hồi: " + response.code());
                    onError.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                Log.e("FeedbackDebug", "Lỗi mạng khi thêm phản hồi: " + t.getMessage());
                onError.run();
            }
        });
    }
    public void deleteFeedback(int feedbackId, Runnable onSuccess, Runnable onError) {
        _isLoading.setValue(true);

        feedbackRepository.deleteFeedback(feedbackId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    _createSuccess.setValue(true);
                    onSuccess.run();
                } else {
                    _createSuccess.setValue(false);
                    onError.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _createSuccess.setValue(false);
                onError.run();
            }
        });
    }

    public void updateFeedback(int feedbackId, String description, Runnable onSuccess, Runnable onError) {
        _isLoading.setValue(true);

        FeedbackRequestDTO request = new FeedbackRequestDTO(description);

        feedbackRepository.updateFeedback(feedbackId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    _createSuccess.setValue(true);
                    onSuccess.run();
                } else {
                    _createSuccess.setValue(false);
                    onError.run();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _createSuccess.setValue(false);
                onError.run();
            }
        });
    }

}
