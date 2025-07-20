package com.example.bakeryshop.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bakeryshop.Data.DTO.ReadOrderDetailDTO;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.Data.Repository.OrderRepository;
import com.example.bakeryshop.Data.Repository.ProductRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryDetailViewModel extends AndroidViewModel {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final MutableLiveData<List<ReadOrderDetailDTO>> orderDetails = new MutableLiveData<>();
    public LiveData<List<ReadOrderDetailDTO>> _orderDetails = orderDetails;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public LiveData<Boolean> _isLoading = isLoading;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public LiveData<String> _errorMessage = errorMessage;

    public OrderHistoryDetailViewModel(Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
        productRepository = new ProductRepository(application);
    }

    public void fetchOrderDetails(int orderId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        orderRepository.getOrderDetailsByOrderId(orderId).enqueue(new Callback<List<ReadOrderDetailDTO>>() {
            @Override
            public void onResponse(Call<List<ReadOrderDetailDTO>> call, Response<List<ReadOrderDetailDTO>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Log.d("OrderDetail", "onResponse: " + response.body().get(0).orderDetailID);
                    List<ReadOrderDetailDTO> tempDetails = response.body();
                    orderDetails.setValue(tempDetails); // tạm gán để hiển thị ngay

                    for (int i = 0; i < tempDetails.size(); i++) {
                        final int index = i;
                        ReadOrderDetailDTO item = tempDetails.get(index);

                        productRepository.getProductById(item.getProductID()).enqueue(new Callback<ReadProductDTO>() {
                            @Override
                            public void onResponse(@NonNull Call<ReadProductDTO> call, @NonNull Response<ReadProductDTO> productResponse) {
                                if (productResponse.isSuccessful() && productResponse.body() != null) {
                                    ReadProductDTO product = productResponse.body();

                                    item.setProductName(product.getProductName());
                                    item.setProductImg(product.getImageUrl());

                                    tempDetails.set(index, item); // cập nhật dữ liệu từng dòng
                                    orderDetails.postValue(tempDetails); // cập nhật lại để UI render
                                    Log.d("OrderDetail", "✅ Đã gán sản phẩm: " + product.getProductName());
                                } else {
                                    Log.e("OrderDetail", "❌ Lỗi phản hồi khi lấy sản phẩm ID: " + item.getProductID());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ReadProductDTO> call, @NonNull Throwable t) {
                                Log.e("OrderDetail", "💥 Lỗi kết nối sản phẩm: " + t.getMessage());
                                errorMessage.postValue("Lỗi kết nối sản phẩm: " + t.getMessage());
                            }
                        });
                    }

                } else {
                    errorMessage.setValue("Không thể tải chi tiết đơn hàng.");
                    Log.e("OrderDetail", "❌ Phản hồi rỗng hoặc lỗi.");
                }
            }

            @Override
            public void onFailure(Call<List<ReadOrderDetailDTO>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối đơn hàng: " + t.getMessage());
                Log.e("OrderDetail", "💥 Lỗi kết nối API đơn hàng: " + t.getMessage());
            }
        });
    }
}