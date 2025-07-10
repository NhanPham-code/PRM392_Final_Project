package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.Api.ApiClient;
import com.example.bakeryshop.Data.Api.ApiService;
import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.Data.DTO.CartItemDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.DTO.UpdateCartQuantityRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final MutableLiveData<List<CartDisplayItem>> _cartItems = new MutableLiveData<>();
    public LiveData<List<CartDisplayItem>> cartItems = _cartItems;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    private Map<Integer, Integer> initialQuantities = new HashMap<>();

    public CartViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance(application).getApiService();
    }

    public void fetchCartItems() {
        _isLoading.setValue(true);
        apiService.getCartByToken().enqueue(new Callback<List<CartItemDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<CartItemDTO>> call, @NonNull Response<List<CartItemDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CartItemDTO> cartItemDTOS = response.body();
                    List<CartDisplayItem> displayItems = new ArrayList<>();
                    initialQuantities.clear();
                    // Lưu ý: Cần một cách để chờ tất cả ProductDetails được fetch xong.
                    // Sử dụng AtomicInteger hoặc đếm số lượng response.
                    // Đơn giản hơn: fetch product list trước, rồi mới kết hợp.
                    // Tạm thời, tôi giữ nguyên cách bạn làm để tập trung vào lỗi hiện tại.
                    if (cartItemDTOS.isEmpty()) {
                        _cartItems.setValue(new ArrayList<>()); // Đặt danh sách rỗng nếu không có gì
                        _isLoading.setValue(false);
                    } else {
                        // Tạo một đối tượng đếm để biết khi nào tất cả sản phẩm đã được fetch
                        final int[] fetchedProductsCount = {0};
                        for (CartItemDTO cartItem : cartItemDTOS) {
                            fetchProductDetails(cartItem, displayItems, cartItemDTOS.size(), fetchedProductsCount);
                            initialQuantities.put(cartItem.getCartID(), cartItem.getQuantity());
                        }
                    }

                } else {
                    Log.e("CartViewModel", "Failed to get cart items. Code: " + response.code() + ", Message: " + response.message());
                    _error.setValue("Không lấy được giỏ hàng. Mã lỗi: " + response.code());
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
        apiService.getProductById(cartItem.getProductID()).enqueue(new Callback<ReadProductDTO>() {
            @Override
            public void onResponse(@NonNull Call<ReadProductDTO> call, @NonNull Response<ReadProductDTO> response) {
                synchronized (fetchedProductsCount) { // Đồng bộ hóa việc đếm
                    if (response.isSuccessful() && response.body() != null) {
                        displayItems.add(new CartDisplayItem(cartItem, response.body()));
                    } else {
                        Log.e("CartViewModel", "Failed to get product details for item " + cartItem.getProductID() + ". Code: " + response.code());
                        // Có thể thêm một placeholder item hoặc bỏ qua item này
                        _error.setValue("Không lấy được chi tiết sản phẩm cho id: " + cartItem.getProductID());
                    }
                    fetchedProductsCount[0]++;
                    if (fetchedProductsCount[0] == totalCartItems) {
                        // Sắp xếp lại danh sách nếu cần thiết (ví dụ theo cartId)
                        // Collections.sort(displayItems, (item1, item2) -> Integer.compare(item1.getCartItem().getCartId(), item2.getCartItem().getCartId()));
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
                        _cartItems.setValue(displayItems); // Vẫn hiển thị những gì đã fetch được
                        _isLoading.setValue(false);
                    }
                }
            }
        });
    }


    // MỚI: Phương thức để tăng/giảm số lượng của một sản phẩm trong ViewModel
    // ViewModel chỉ chịu trách nhiệm cập nhật dữ liệu, giới hạn đã xử lý ở Adapter
    public void updateQuantityLocally(CartDisplayItem item, int change) {
        int newQuantity = item.getCartItem().getQuantity() + change;
        if (newQuantity < 1) newQuantity = 1; // Giới hạn dưới 1
        // Không cần kiểm tra stockQuantity ở đây vì Adapter đã xử lý rồi
        item.getCartItem().setQuantity(newQuantity);
        _cartItems.setValue(_cartItems.getValue()); // Kích hoạt observer để UI cập nhật (nếu Adapter không cập nhật trực tiếp)
    }

    public void deleteCartItem(CartDisplayItem itemToDelete) {
        _isLoading.setValue(true);
        apiService.deleteCartItem(itemToDelete.getCartItem().getCartID()).enqueue(new Callback<Void>() {
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
                    _error.setValue("Đã xóa sản phẩm khỏi giỏ hàng.");
                } else {
                    Log.e("CartViewModel", "Failed to delete cart item. Code: " + response.code() + ", Message: " + response.message());
                    _error.setValue("Không thể xóa sản phẩm. Mã lỗi: " + response.code());
                }
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("CartViewModel", "API Call for deleteCartItem failed: " + t.getMessage(), t);
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

                // Chỉ thêm vào danh sách update nếu số lượng đã thay đổi
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
        apiService.updateCartQuantities(updates).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CartViewModel", "Cart quantities updated successfully.");
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
                    _error.setValue("Cập nhật giỏ hàng thất bại. Mã lỗi: " + response.code());
                }
                _isLoading.setValue(false);
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("CartViewModel", "API Call for updateCartQuantities failed: " + t.getMessage(), t);
                _error.setValue("Lỗi kết nối khi cập nhật giỏ hàng: " + t.getMessage());
                _isLoading.setValue(false);
            }
        });
    }
}