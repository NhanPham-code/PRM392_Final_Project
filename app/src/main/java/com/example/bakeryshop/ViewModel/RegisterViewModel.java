package com.example.bakeryshop.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.Data.DTO.RegisterRequestDTO;
import com.example.bakeryshop.Data.Repository.UserRepository;

public class RegisterViewModel extends AndroidViewModel {

    private UserRepository userRepository;

    // LiveData để thông báo trạng thái đăng ký thành công
    private final MutableLiveData<Boolean> _registerSuccess = new MutableLiveData<>();
    public LiveData<Boolean> registerSuccess = _registerSuccess;

    // LiveData để thông báo trạng thái tải (loading)
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    // LiveData để thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public RegisterViewModel(Application application) {
        super(application);
        // Initialize any necessary components here
        this.userRepository = new UserRepository();
    }

    public void register(String fullName, String email, String password, String address, String phoneNumber) {
        _isLoading.setValue(true);
        _registerSuccess.setValue(false);
        clearErrorMessage();

        // Create a RegisterRequestDTO object
        RegisterRequestDTO request = new RegisterRequestDTO(
                fullName,
                email,
                password,
                "Customer", // Assuming role is always "Customer"
                phoneNumber,
                address
        );

        // Call the repository to register the user
        userRepository.register(request).enqueue(new retrofit2.Callback<ReadUserDTO>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ReadUserDTO> call, @NonNull retrofit2.Response<ReadUserDTO> response) {
                _isLoading.setValue(false);
                if (response.isSuccessful()) {
                    _registerSuccess.setValue(true);
                } else {
                    _errorMessage.setValue("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ReadUserDTO> call, @NonNull Throwable t) {
                _isLoading.setValue(false);
                _errorMessage.setValue("Error: " + t.getMessage());
            }
        });
    }

    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }
}
