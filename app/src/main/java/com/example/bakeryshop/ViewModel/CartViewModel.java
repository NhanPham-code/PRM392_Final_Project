package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.Api.ApiClient; // Vẫn cần cho Repository
import com.example.bakeryshop.Data.Api.ApiService; // Vẫn cần cho Repository
import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.Data.DTO.CartItemDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.DTO.UpdateCartQuantityRequest;
import com.example.bakeryshop.Data.Repository.CartRepository; // MỚI: Import CartRepository

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends AndroidViewModel {

    // THAY THẾ ApiService BẰNG CartRepository
    private final CartRepository cartRepository; // Đổi từ ApiService sang CartRepository

    private final MutableLiveData<List<CartDisplayItem>> _cartItems = new MutableLiveData<>();
    public LiveData<List<CartDisplayItem>> cartItems = _cartItems;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    // LiveData riêng để báo cáo thành công/thất bại cho các thao tác CRUD cụ thể (tùy chọn)
    private final MutableLiveData<Boolean> _deleteSuccess = new MutableLiveData<>();
    public LiveData<Boolean> deleteSuccess = _deleteSuccess;

    private final MutableLiveData<Boolean> _updateSuccess = new MutableLiveData<>();
    public LiveData<Boolean> updateSuccess = _updateSuccess;


    private Map<Integer, Integer> initialQuantities = new HashMap<>();

    public CartViewModel(@NonNull Application application) {
        super(application);
        // KHỞI TẠO CartRepository thay vì ApiService
        cartRepository = new CartRepository(application.getApplicationContext());
    }

    public void fetchCartItems() {
        _isLoading.setValue(true);
        // GỌI PHƯƠNG THỨC TỪ Repository
        cartRepository.getCartByUser().enqueue(new Callback<List<CartItemDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CartItemDTO>> call, @NonNull Response<List<CartItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItemDTO> cartItemDTOS = response.body();
                    List<CartDisplayItem> displayItems = new ArrayList<>();
                    initialQuantities.clear();

                    if (cartItemDTOS.isEmpty()) {
                        _cartItems.setValue(new ArrayList<>());
                        _isLoading.setValue(false);
                    } else {
                        final int[] fetchedProductsCount = {0};
                        for (CartItemDTO cartItem : cartItemDTOS) {
                            // GỌI PHƯƠNG THỨC TỪ Repository
                            fetchProductDetails(cartItem, displayItems, cartItemDTOS.size(), fetchedProductsCount);
                            initialQuantities.put(cartItem.getCartID(), cartItem.getQuantity());
                        }
                    }

                } else {
                    Log.e("CartViewModel", "Failed to get cart items. Code: " + response.code() + ", Message: " + response.message());
                    String errorMessage = "Không lấy được giỏ hàng.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("CartViewModel", "Error parsing error body: " + e.getMessage());
                    }
                    _error.setValue(errorMessage);
                    _isLoading.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CartItemDTO>> call, @NonNull Throwable t) {
                Log.e("CartViewModel", "API Call for getCartByToken failed: " + t.getMessage(), t);
                _error.setValue("Lỗi kết nối khi lấy giỏ hàng: " + t.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    private void fetchProductDetails(CartItemDTO cartItem, List<CartDisplayItem> displayItems, int totalCartItems, int[] fetchedProductsCount) {
        // GỌI PHƯƠNG THỨC TỪ Repository
        cartRepository.getProductById(cartItem.getProductID()).enqueue(new Callback<ReadProductDTO>() {
            @Override
            public void onResponse(@NonNull Call<ReadProductDTO> call, @NonNull Response<ReadProductDTO> response) {
                synchronized (fetchedProductsCount) {
                    if (response.isSuccessful() && response.body() != null) {
                        displayItems.add(new CartDisplayItem(cartItem, response.body()));
                    } else {
                        Log.e("CartViewModel", "Failed to get product details for item " + cartItem.getProductID() + ". Code: " + response.code());
                        String errorMessage = "Không lấy được chi tiết sản phẩm cho id: " + cartItem.getProductID();
                        try {
                            if (response.errorBody() != null) {
                                errorMessage += " - " + response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e("CartViewModel", "Error parsing error body for product details: " + e.getMessage());
                        }
                        _error.setValue(errorMessage);
                    }
                    fetchedProductsCount[0]++;
                    if (fetchedProductsCount[0] == totalCartItems) {
                        _cartItems.setValue(displayItems);
                        _isLoading.setValue(false);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReadProductDTO> call, @NonNull Throwable t) {
                synchronized (fetchedProductsCount) {
                    Log.e("CartViewModel", "API Call for getProductById failed for item " + cartItem.getProductID() + ": " + t.getMessage(), t);
                    _error.setValue("Lỗi kết nối khi lấy chi tiết sản phẩm: " + t.getMessage());
                    fetchedProductsCount[0]++;
                    if (fetchedProductsCount[0] == totalCartItems) {
                        _cartItems.setValue(displayItems);
                        _isLoading.setValue(false);
                    }
                }
            }
        });
    }


    public void updateQuantityLocally(CartDisplayItem item, int change) {
        int newQuantity = item.getCartItem().getQuantity() + change;
        if (newQuantity < 1) newQuantity = 1; // Giới hạn dưới 1
        item.getCartItem().setQuantity(newQuantity);
        _cartItems.setValue(_cartItems.getValue());
    }

    public void deleteCartItem(CartDisplayItem itemToDelete) {
        _isLoading.setValue(true);
        _deleteSuccess.setValue(null); // Reset trạng thái trước khi gọi API
        // GỌI PHƯƠNG THỨC TỪ Repository
        cartRepository.deleteCartItem(itemToDelete.getCartItem().getCartID()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CartViewModel", "Cart item deleted successfully: " + itemToDelete.getCartItem().getCartID());
                    List<CartDisplayItem> currentItems = _cartItems.getValue();
                    if (currentItems != null) {
                        List<CartDisplayItem> updatedItems = new ArrayList<>(currentItems);
                        updatedItems.remove(itemToDelete);
                        _cartItems.setValue(updatedItems);
                    }
                    _deleteSuccess.setValue(true); // Báo cáo thành công
                    _error.setValue("Đã xóa sản phẩm khỏi giỏ hàng."); // Thông báo lỗi (có thể dùng LiveData riêng cho thông báo)
                } else {
                    Log.e("CartViewModel", "Failed to delete cart item. Code: " + response.code() + ", Message: " + response.message());
                    String errorMessage = "Không thể xóa sản phẩm.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("CartViewModel", "Error parsing error body for delete: " + e.getMessage());
                    }
                    _deleteSuccess.setValue(false); // Báo cáo thất bại
                    _error.setValue(errorMessage);
                }
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("CartViewModel", "API Call for deleteCartItem failed: " + t.getMessage(), t);
                _deleteSuccess.setValue(false); // Báo cáo thất bại
                _error.setValue("Lỗi kết nối khi xóa sản phẩm: " + t.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    public void updateAllCartQuantities() {
        List<UpdateCartQuantityRequest> updates = new ArrayList<>();
        List<CartDisplayItem> currentItems = _cartItems.getValue();

        if (currentItems != null) {
            for (CartDisplayItem item : currentItems) {
                int cartId = item.getCartItem().getCartID();
                int currentQuantity = item.getCartItem().getQuantity();
                Integer initialQuantity = initialQuantities.get(cartId);

                if (initialQuantity == null || currentQuantity != initialQuantity) {
                    updates.add(new UpdateCartQuantityRequest(cartId, currentQuantity));
                }
            }
        }

        if (updates.isEmpty()) {
            _error.setValue("Không có sản phẩm nào thay đổi số lượng.");
            return;
        }

        _isLoading.setValue(true);
        _updateSuccess.setValue(null); // Reset trạng thái trước khi gọi API
        // GỌI PHƯƠNG THỨC TỪ Repository
        cartRepository.updateCartQuantities(updates).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CartViewModel", "Cart quantities updated successfully.");
                    _updateSuccess.setValue(true); // Báo cáo thành công
                    _error.setValue("Cập nhật giỏ hàng thành công!");
                    // Cập nhật lại initialQuantities sau khi update thành công
                    if (currentItems != null) {
                        initialQuantities.clear();
                        for (CartDisplayItem item : currentItems) {
                            initialQuantities.put(item.getCartItem().getCartID(), item.getCartItem().getQuantity());
                        }
                    }
                } else {
                    Log.e("CartViewModel", "Failed to update cart quantities. Code: " + response.code() + ", Message: " + response.message());
                    String errorMessage = "Cập nhật giỏ hàng thất bại.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("CartViewModel", "Error parsing error body for update: " + e.getMessage());
                    }
                    _updateSuccess.setValue(false); // Báo cáo thất bại
                    _error.setValue(errorMessage);
                }
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("CartViewModel", "API Call for updateCartQuantities failed: " + t.getMessage(), t);
                _updateSuccess.setValue(false); // Báo cáo thất bại
                _error.setValue("Lỗi kết nối khi cập nhật giỏ hàng: " + t.getMessage());
                _isLoading.setValue(false);
            }
        });
    }

    // Các phương thức reset trạng thái nếu bạn sử dụng LiveData riêng cho thông báo thành công
    public void resetDeleteStatus() {
        _deleteSuccess.setValue(null);
    }

    public void resetUpdateStatus() {
        _updateSuccess.setValue(null);
    }
}