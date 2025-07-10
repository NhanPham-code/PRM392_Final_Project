package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bakeryshop.Data.DTO.LoginRequestDTO;
import com.example.bakeryshop.Data.DTO.LoginResponseDTO;
import com.example.bakeryshop.Data.Repository.UserRepository;

import java.io.Console;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    // Instance của UserRepository để gọi API
    private final UserRepository userRepository;

    // LiveData để thông báo trạng thái đăng nhập về UI
    // MutableLiveData có thể thay đổi giá trị từ ViewModel
    private final MutableLiveData<Boolean> _loginSuccess = new MutableLiveData<>();
    // LiveData công khai chỉ để đọc từ UI
    public LiveData<Boolean> loginSuccess = _loginSuccess;

    // LiveData để thông báo trạng thái tải (loading)
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    // LiveData để thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    // SharedPreferences để lưu trữ token
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";

    // Constructor của ViewModel
    public LoginViewModel(Application application) {
        super(application);
        // Khởi tạo UserRepository
        this.userRepository = new UserRepository(application);
    }

    /**
     * Phương thức xử lý logic đăng nhập.
     * Cập nhật trạng thái loading, gọi API, và xử lý kết quả.
     *
     * @param email Tên đăng nhập hoặc email của người dùng.
     * @param password Mật khẩu của người dùng.
     */
    public void login(String email, String password) {
        // Bắt đầu quá trình đăng nhập, hiển thị trạng thái loading
        _isLoading.setValue(true);
        // Reset lỗi và kết quả đăng nhập trước khi gọi API mới
        _errorMessage.setValue(null);
        _loginSuccess.setValue(false);

        // Tạo đối tượng yêu cầu đăng nhập
        LoginRequestDTO requestDTO = new LoginRequestDTO(email, password, "Customer");

        // Gọi API đăng nhập từ UserRepository
        userRepository.login(requestDTO).enqueue(new Callback<LoginResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponseDTO> call, @NonNull Response<LoginResponseDTO> response) {
                // Kết thúc trạng thái loading
                _isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Save token
                    LoginResponseDTO loginResponseDTO = response.body();
                    SaveToken(loginResponseDTO.getToken());
                    Log.d("LoginViewModel", "Token saved: " + loginResponseDTO.getToken());
                    // Đăng nhập thành công
                    _loginSuccess.setValue(true);
                } else {
                    // Đăng nhập thất bại (lỗi từ server, ví dụ: sai thông tin đăng nhập)
                    String error = "Lỗi đăng nhập. Vui lòng thử lại.";
                    if (response.errorBody() != null) {
                        try {
                            // Cố gắng đọc thông báo lỗi từ response body
                            error = response.errorBody().string();
                            // Bạn có thể parse JSON nếu server trả về lỗi dạng JSON
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    _errorMessage.setValue(error);
                    _loginSuccess.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponseDTO> call, @NonNull Throwable t) {
                // Kết thúc trạng thái loading
                _isLoading.setValue(false);
                // Xảy ra lỗi mạng hoặc lỗi không xác định
                _errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
                Log.d("Fail", "onFailure: " + t.getMessage());
                _loginSuccess.setValue(false);
            }
        });
    }
    /**
     * Lưu token vào SharedPreferences.
     */
    private void SaveToken(String token) {
        // Lấy context từ Application để khởi tạo SharedPreferences
        sharedPreferences = getApplication().getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Lưu token vào SharedPreferences
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * Phương thức công khai để xóa thông báo lỗi sau khi nó được hiển thị trên UI.
     */
    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }
}
