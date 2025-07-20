package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.OrderResponse;
import com.example.bakeryshop.Data.DTO.ReadOrderDTO;
import com.example.bakeryshop.Data.DTO.ReadOrderHistoryDTO;
import com.example.bakeryshop.Data.Repository.OrderRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryViewModel extends AndroidViewModel {
    private final OrderRepository orderRepository;
    private final MutableLiveData<List<ReadOrderHistoryDTO>> orderHistory = new MutableLiveData<>();
    public LiveData<List<ReadOrderHistoryDTO>> _orderHistory = orderHistory;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public LiveData<Boolean> _isLoading = isLoading;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> _errorMessage = errorMessage;

    public OrderHistoryViewModel(Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }

    public void fetchOrderHistory() {
        isLoading.setValue(true);
        clearErrorMessage();

        Log.d("OrderHistoryViewModel", "Bắt đầu tải danh sách đơn hàng...");

        orderRepository.getAllOrderByUser().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderHistory.setValue(response.body().getValue());
                } else {
                    errorMessage.setValue("Không thể tải dữ liệu đơn hàng.");
                    Log.e("OrderHistoryViewModel", "Phản hồi không hợp lệ hoặc rỗng.");
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối mạng: " + t.getMessage());
                Log.e("OrderHistoryViewModel", "Lỗi kết nối: " + t.getMessage(), t);
            }
        });
    }

    public void clearErrorMessage() {
        errorMessage.setValue(null);
    }
}