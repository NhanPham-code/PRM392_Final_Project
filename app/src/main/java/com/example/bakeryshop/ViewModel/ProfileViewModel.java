package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.Data.DTO.UpdateUserProfileDTO;
import com.example.bakeryshop.Data.Repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";

    private UserRepository userRepository;

    private final MutableLiveData<ReadUserDTO> _getProfileSuccess = new MutableLiveData<>();
    // LiveData công khai chỉ để đọc từ UI
    public LiveData<ReadUserDTO> getProfileSuccess = _getProfileSuccess;

    private final MutableLiveData<Boolean> _updateProfileSuccess = new MutableLiveData<>();
    // LiveData công khai chỉ để đọc từ UI
    public LiveData<Boolean> updateProfileSuccess = _updateProfileSuccess;

    // LiveData để thông báo trạng thái tải (loading)
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    // LiveData để thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public ProfileViewModel(Application application) {
        super(application);
        // Initialize the UserRepository
        userRepository = new UserRepository(application);
    }

    /**
     * Method to fetch user profile information.
     * Updates loading state and handles the result.
     */
    public void fetchUserProfile() {
        _isLoading.setValue(true);

        userRepository.getUserInfo() .enqueue(new Callback<ReadUserDTO>() {
            @Override
            public void onResponse(@NonNull Call<ReadUserDTO> call, @NonNull Response<ReadUserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _getProfileSuccess.setValue(response.body());
                    _isLoading.setValue(false);
                } else {
                    // Handle the case where the response is not successful
                    _errorMessage.setValue("Failed to fetch user profile");
                    _isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadUserDTO> call, @NonNull Throwable t) {
                // Handle the failure case
                _errorMessage.setValue("Error: " + t.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    /**
     * Method to update user profile information.
     * @param updateUserProfileDTO The DTO containing updated user information.
     */
    public void updateUserProfile(UpdateUserProfileDTO updateUserProfileDTO) {
        _isLoading.setValue(true);

        // Call the repository method to update user profile information to the server
        userRepository.updateUserProfile(updateUserProfileDTO).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Successfully updated profile
                    _isLoading.setValue(false);

                    // Update the success LiveData
                    _updateProfileSuccess.setValue(true);
                } else {
                    // Handle the case where the update is not successful
                    _errorMessage.setValue("Failed to update profile");
                    _isLoading.setValue(false);

                    // update the success LiveData to false
                    _updateProfileSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Handle the failure case
                _errorMessage.setValue("Error: " + t.getMessage());
                _isLoading.setValue(false);

                // update the success LiveData to false
                _updateProfileSuccess.setValue(false);
            }
        });
    }

    /**
     * Set user profile MutableLiveData
     * @param userProfile The ReadUserDTO object containing user profile data
     */
    public void setUserProfile(ReadUserDTO userProfile) {
        _getProfileSuccess.setValue(userProfile);
    }
}
