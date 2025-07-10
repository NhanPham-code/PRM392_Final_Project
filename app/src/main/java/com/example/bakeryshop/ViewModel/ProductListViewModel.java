package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.Repository.ProductRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListViewModel extends AndroidViewModel {

    private ProductRepository productRepository;

    // MutableLiveData để chứa danh sách sản phẩm
    private final MutableLiveData<List<ReadProductDTO>> _products = new MutableLiveData<>();
    // LiveData công khai để UI quan sát danh sách sản phẩm
    public LiveData<List<ReadProductDTO>> products = _products;

    // MutableLiveData để thông báo trạng thái tải
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    // LiveData công khai để UI quan sát trạng thái tải
    public LiveData<Boolean> isLoading = _isLoading;

    // MutableLiveData để thông báo lỗi
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    // LiveData công khai để UI quan sát thông báo lỗi
    public LiveData<String> errorMessage = _errorMessage;

    public ProductListViewModel(Application application) {
        super(application);
        // Initialize any necessary components here, such as repositories or services.
        productRepository = new ProductRepository(application);
    }

    /**
     * Phương thức này thực hiện việc tải danh sách sản phẩm từ Repository.
     * Cập nhật trạng thái loading, dữ liệu sản phẩm, và thông báo lỗi.
     */
    public void fetchProducts() {
        _isLoading.setValue(true); // Bắt đầu tải, hiển thị loading
        clearErrorMessage(); // Xóa lỗi cũ
        Log.d("ProductListViewModel", "Bắt đầu tải danh sách sản phẩm...");

        productRepository.getProducts().enqueue(new Callback<List<ReadProductDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReadProductDTO>> call, @NonNull Response<List<ReadProductDTO>> response) {
                _isLoading.setValue(false); // Kết thúc tải

                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(response.body()); // Cập nhật danh sách sản phẩm
                    Log.d("ProductListViewModel", "Tải sản phẩm thành công. Số lượng: " + response.body().size());
                } else {
                    String error = "Không thể tải sản phẩm.";
                    if (response.errorBody() != null) {
                        try {
                            error = response.errorBody().string();
                            Log.e("ProductListViewModel", "Lỗi phản hồi API: " + error);
                        } catch (Exception e) {
                            Log.e("ProductListViewModel", "Lỗi khi đọc errorBody: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    _errorMessage.setValue(error); // Cập nhật thông báo lỗi
                    Log.e("ProductListViewModel", "Tải sản phẩm thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReadProductDTO>> call, @NonNull Throwable t) {
                _isLoading.setValue(false); // Kết thúc tải
                _errorMessage.setValue("Lỗi kết nối mạng: " + t.getMessage()); // Cập nhật thông báo lỗi
                Log.e("ProductListViewModel", "Lỗi kết nối khi tải sản phẩm: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Phương thức công khai để xóa thông báo lỗi sau khi nó được hiển thị trên UI.
     */
    public void clearErrorMessage() {
        _errorMessage.setValue(null);
    }

    /**
     * Phương thức để tải danh sách sản phẩm theo ID danh mục.
     * @param categoryId ID của danh mục sản phẩm.
     */
    public void fetchProductsByCategoryId(int categoryId) {
        _isLoading.setValue(true); // Bắt đầu tải, hiển thị loading
        clearErrorMessage(); // Xóa lỗi cũ
        Log.d("ProductListViewModel", "Bắt đầu tải sản phẩm theo danh mục ID: " + categoryId);

        productRepository.getProductsByCategoryId(categoryId).enqueue(new Callback<List<ReadProductDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReadProductDTO>> call, @NonNull Response<List<ReadProductDTO>> response) {
                _isLoading.setValue(false); // Kết thúc tải

                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(response.body()); // Cập nhật danh sách sản phẩm
                    Log.d("ProductListViewModel", "Tải sản phẩm theo danh mục thành công. Số lượng: " + response.body().size());
                } else {
                    String error = "Không thể tải sản phẩm theo danh mục.";
                    if (response.errorBody() != null) {
                        try {
                            error = response.errorBody().string();
                            Log.e("ProductListViewModel", "Lỗi phản hồi API: " + error);
                        } catch (Exception e) {
                            Log.e("ProductListViewModel", "Lỗi khi đọc errorBody: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    _errorMessage.setValue(error); // Cập nhật thông báo lỗi
                    Log.e("ProductListViewModel", "Tải sản phẩm theo danh mục thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReadProductDTO>> call, @NonNull Throwable t) {
                _isLoading.setValue(false); // Kết thúc tải
                _errorMessage.setValue("Lỗi kết nối mạng: " + t.getMessage()); // Cập nhật thông báo lỗi
                Log.e("ProductListViewModel", "Lỗi kết nối khi tải sản phẩm theo danh mục: " + t.getMessage(), t);
            }
        });
    }

    /**
     * Phương thức để tìm kiếm sản phẩm theo từ khóa.
     * @param searchKey Từ khóa tìm kiếm.
     */
    public void searchProducts(String searchKey) {
        _isLoading.setValue(true); // Bắt đầu tải, hiển thị loading
        clearErrorMessage(); // Xóa lỗi cũ
        Log.d("ProductListViewModel", "Bắt đầu tìm kiếm sản phẩm với từ khóa: " + searchKey);

        productRepository.searchProducts(searchKey).enqueue(new Callback<List<ReadProductDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReadProductDTO>> call, @NonNull Response<List<ReadProductDTO>> response) {
                _isLoading.setValue(false); // Kết thúc tải

                if (response.isSuccessful() && response.body() != null) {
                    _products.setValue(response.body()); // Cập nhật danh sách sản phẩm
                    Log.d("ProductListViewModel", "Tìm kiếm sản phẩm thành công. Số lượng: " + response.body().size());
                } else {
                    String error = "Không thể tìm kiếm sản phẩm.";
                    if (response.errorBody() != null) {
                        try {
                            error = response.errorBody().string();
                            Log.e("ProductListViewModel", "Lỗi phản hồi API: " + error);
                        } catch (Exception e) {
                            Log.e("ProductListViewModel", "Lỗi khi đọc errorBody: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    _errorMessage.setValue(error); // Cập nhật thông báo lỗi
                    Log.e("ProductListViewModel", "Tìm kiếm sản phẩm thất bại. Mã lỗi: " + response.code() + ", Lỗi: " + error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReadProductDTO>> call, @NonNull Throwable t) {
                _isLoading.setValue(false); // Kết thúc tải
                _errorMessage.setValue("Lỗi kết nối mạng: " + t.getMessage()); // Cập nhật thông báo lỗi
                Log.e("ProductListViewModel", "Lỗi kết nối khi tìm kiếm sản phẩm: " + t.getMessage(), t);
            }
        });
    }
}
