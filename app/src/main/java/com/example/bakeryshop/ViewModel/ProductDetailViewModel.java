package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.Repository.ProductRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailViewModel extends AndroidViewModel {

    // TAG để dễ dàng theo dõi log
    private static final String TAG = "ProductDetailViewModel";

    private final ProductRepository productRepository; // Đặt final
    private final SharedPreferences sharedPreferences; // Đặt final và khởi tạo trong constructor

    private final MutableLiveData<ReadProductDTO> _productSuccess = new MutableLiveData<>();
    public LiveData<ReadProductDTO> productSuccess = _productSuccess;

    private final MutableLiveData<Boolean> _productLoading = new MutableLiveData<>();
    public LiveData<Boolean> productLoading = _productLoading;

    private final MutableLiveData<String> _productError = new MutableLiveData<>();
    public LiveData<String> productError = _productError;

    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";

    public ProductDetailViewModel(@NonNull Application application){ // Thêm @NonNull
        super(application);
        this.productRepository = new ProductRepository();
        // Khởi tạo SharedPreferences một lần trong constructor
        this.sharedPreferences = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);
    }

    private String getToken() {
        // Chỉ cần trả về token từ SharedPreferences đã được khởi tạo
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clearErrorMessage() {
        _productError.setValue(null);
    }

    public void fetchProductsByProductID(int productID) {
        _productLoading.setValue(true); // Bắt đầu tải, hiển thị loading
        clearErrorMessage(); // Xóa lỗi cũ
        Log.d(TAG, "Bắt đầu tải sản phẩm theo Product ID: " + productID);

        // Nếu API yêu cầu token, bạn sẽ truyền nó vào đây
        // String authToken = getToken();
        // productRepository.getProductById(authToken, productID).enqueue(...);
        // Hiện tại, không thấy việc sử dụng token, nên giữ nguyên
        productRepository.getProductById(productID).enqueue(new Callback<ReadProductDTO>() {
            @Override
            public void onResponse(@NonNull Call<ReadProductDTO> call, @NonNull Response<ReadProductDTO> response) {
                _productLoading.setValue(false); // Kết thúc tải
                if (response.isSuccessful() && response.body() != null) {
                    _productSuccess.setValue(response.body()); // Cập nhật chi tiết sản phẩm
                    Log.d(TAG, "Tải sản phẩm thành công.");
                } else {
                    String error = "Không thể tải sản phẩm.";
                    if (response.errorBody() != null) {
                        try {
                            // Gọi .string() một lần và lưu vào biến
                            String errorResponse = response.errorBody().string();
                            error = errorResponse;
                            Log.e(TAG, "Lỗi phản hồi API: " + errorResponse);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e); // Log cả exception
                            // Giữ thông báo lỗi mặc định nếu không đọc được errorBody
                        }
                    } else {
                        // Trường hợp errorBody là null nhưng response không thành công
                        error = "Phản hồi lỗi không có nội dung.";
                    }
                    Log.e(TAG, "Tải sản phẩm thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                    _productError.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadProductDTO> call, @NonNull Throwable t) {
                _productLoading.setValue(false); // Kết thúc tải
                // Cập nhật thông báo lỗi, thêm check null cho t.getMessage()
                String errorMessage = (t.getMessage() != null) ? t.getMessage() : "Lỗi không xác định.";
                _productError.setValue("Lỗi kết nối mạng: " + errorMessage);
                Log.e(TAG, "Lỗi kết nối khi tải sản phẩm: " + t.getMessage(), t); // Log cả Throwable t
            }
        });
    }
}