package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.AddCartItemRequestDTO; // MỚI
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.Repository.CartRepository;
import com.example.bakeryshop.Data.Repository.ProductRepository;
import com.example.bakeryshop.Data.Api.ApiService; // MỚI
import com.example.bakeryshop.Data.Api.ApiClient; // MỚI

import java.util.List; // MỚI

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailViewModel extends AndroidViewModel {

    private static final String TAG = "ProductDetailViewModel";

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final SharedPreferences sharedPreferences;

    private final MutableLiveData<ReadProductDTO> _productSuccess = new MutableLiveData<>();
    public LiveData<ReadProductDTO> productSuccess = _productSuccess;

    private final MutableLiveData<List<ReadProductDTO>> _products = new MutableLiveData<>(); // MỚI: Dành cho sản phẩm gợi ý
    public LiveData<List<ReadProductDTO>> products = _products; // MỚI

    private final MutableLiveData<Boolean> _productLoading = new MutableLiveData<>();
    public LiveData<Boolean> productLoading = _productLoading;

    private final MutableLiveData<String> _productError = new MutableLiveData<>();
    public LiveData<String> productError = _productError;

    // MỚI: LiveData cho trạng thái thêm vào giỏ hàng
    private final MutableLiveData<Boolean> _addToCartSuccess = new MutableLiveData<>();
    public LiveData<Boolean> addToCartSuccess = _addToCartSuccess;

    private final MutableLiveData<String> _addToCartError = new MutableLiveData<>();
    public LiveData<String> addToCartError = _addToCartError;


    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";

    public ProductDetailViewModel(@NonNull Application application){
        super(application);
        this.productRepository = new ProductRepository(application);
        this.cartRepository = new CartRepository(application);
        this.sharedPreferences = application.getSharedPreferences(PREFS_NAME, Application.MODE_PRIVATE);
    }

    private String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clearErrorMessage() {
        _productError.setValue(null);
    }

    // Phương thức fetch sản phẩm theo ID (có thể không dùng nếu đã nhận qua Intent)
    public void fetchProductsByProductID(int productID) {
        _productLoading.setValue(true);
        clearErrorMessage();
        Log.d(TAG, "Bắt đầu tải sản phẩm theo Product ID: " + productID);

        productRepository.getProductById(productID).enqueue(new Callback<ReadProductDTO>() {
            @Override
            public void onResponse(@NonNull Call<ReadProductDTO> call, @NonNull Response<ReadProductDTO> response) {
                _productLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _productSuccess.setValue(response.body());
                    Log.d(TAG, "Tải sản phẩm thành công.");
                } else {
                    String error = "Không thể tải sản phẩm.";
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            error = errorResponse;
                            Log.e(TAG, "Lỗi phản hồi API: " + errorResponse);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody: " + e.getMessage(), e);
                        }
                    } else {
                        error = "Phản hồi lỗi không có nội dung.";
                    }
                    Log.e(TAG, "Tải sản phẩm thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                    _productError.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadProductDTO> call, @NonNull Throwable t) {
                _productLoading.setValue(false);
                String errorMessage = (t.getMessage() != null) ? t.getMessage() : "Lỗi không xác định.";
                _productError.setValue("Lỗi kết nối mạng: " + errorMessage);
                Log.e(TAG, "Lỗi kết nối khi tải sản phẩm: " + t.getMessage(), t);
            }
        });
    }

    // MỚI: Phương thức để fetch sản phẩm theo CategoryId (cho gợi ý)
    public void fetchProductsByCategoryId(int categoryId) {
        _productLoading.setValue(true);
        clearErrorMessage();
        Log.d(TAG, "Bắt đầu tải sản phẩm theo Category ID: " + categoryId);

        productRepository.getProductsByCategoryId(categoryId).enqueue(new Callback<List<ReadProductDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReadProductDTO>> call, @NonNull Response<List<ReadProductDTO>> response) {
                _productLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(response.body());
                    Log.d(TAG, "Tải sản phẩm theo Category ID thành công.");
                } else {
                    String error = "Không thể tải sản phẩm gợi ý.";
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            error = errorResponse;
                            Log.e(TAG, "Lỗi phản hồi API (gợi ý): " + errorResponse);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody (gợi ý): " + e.getMessage(), e);
                        }
                    } else {
                        error = "Phản hồi lỗi không có nội dung (gợi ý).";
                    }
                    Log.e(TAG, "Tải sản phẩm gợi ý thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                    _productError.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReadProductDTO>> call, @NonNull Throwable t) {
                _productLoading.setValue(false);
                String errorMessage = (t.getMessage() != null) ? t.getMessage() : "Lỗi không xác định.";
                _productError.setValue("Lỗi kết nối mạng khi tải sản phẩm gợi ý: " + errorMessage);
                Log.e(TAG, "Lỗi kết nối khi tải sản phẩm gợi ý: " + t.getMessage(), t);
            }
        });
    }

    // MỚI: Phương thức để thêm sản phẩm vào giỏ hàng
    public void addItemToCart(int productId, int quantity) {
        _productLoading.setValue(true); // Hiển thị loading khi thêm vào giỏ hàng
        _addToCartSuccess.setValue(null); // Reset trạng thái
        _addToCartError.setValue(null); // Reset lỗi

        AddCartItemRequestDTO request = new AddCartItemRequestDTO(productId, quantity);

        cartRepository.addItemToCart(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                _productLoading.setValue(false); // Ẩn loading
                if (response.isSuccessful()) {
                    _addToCartSuccess.setValue(true);
                    Log.d(TAG, "Thêm sản phẩm " + productId + " với số lượng " + quantity + " vào giỏ hàng thành công.");
                } else {
                    String error = "Không thể thêm sản phẩm vào giỏ hàng.";
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            error = errorResponse;
                            Log.e(TAG, "Lỗi phản hồi API khi thêm vào giỏ hàng: " + errorResponse);
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi khi đọc errorBody (thêm vào giỏ hàng): " + e.getMessage(), e);
                        }
                    } else {
                        error = "Phản hồi lỗi không có nội dung khi thêm vào giỏ hàng.";
                    }
                    _addToCartSuccess.setValue(false);
                    _addToCartError.setValue(error);
                    Log.e(TAG, "Thêm sản phẩm vào giỏ hàng thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                _productLoading.setValue(false); // Ẩn loading
                String errorMessage = (t.getMessage() != null) ? t.getMessage() : "Lỗi không xác định.";
                _addToCartSuccess.setValue(false);
                _addToCartError.setValue("Lỗi kết nối mạng khi thêm vào giỏ hàng: " + errorMessage);
                Log.e(TAG, "Lỗi kết nối khi thêm vào giỏ hàng: " + t.getMessage(), t);
            }
        });
    }

    // MỚI: Phương thức để reset trạng thái thêm vào giỏ hàng
    public void resetAddToCartStatus() {
        _addToCartSuccess.setValue(null);
        _addToCartError.setValue(null);
    }
}