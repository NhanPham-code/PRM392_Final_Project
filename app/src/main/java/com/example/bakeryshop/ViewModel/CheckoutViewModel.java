package com.example.bakeryshop.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.Data.DTO.CreateOrderDTO;
import com.example.bakeryshop.Data.DTO.CreateOrderDetailDTO;
import com.example.bakeryshop.Data.DTO.ReadOrderDTO;
import com.example.bakeryshop.Data.Repository.CartRepository;
import com.example.bakeryshop.Data.Repository.OrderRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutViewModel extends ViewModel {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>();

    public CheckoutViewModel(Context context) {
        this.orderRepository = new OrderRepository(context);
        this.cartRepository = new CartRepository(context);
    }

    public LiveData<Boolean> getOrderSuccess() {
        return orderSuccess;
    }

    public void placeOrder(String paymentMethod, String address, double totalAmount,
                           List<CartDisplayItem> cartItems) {

        CreateOrderDTO orderDTO = new CreateOrderDTO(paymentMethod, address, totalAmount);

        orderRepository.createOrder(orderDTO).enqueue(new Callback<ReadOrderDTO>() {
            @Override
            public void onResponse(Call<ReadOrderDTO> call, Response<ReadOrderDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    orderSuccess.postValue(false);
                    return;
                }

                int orderId = response.body().getOrderID();
                int totalItems = cartItems.size();
                int[] completed = {0};
                boolean[] hasError = {false};

                for (CartDisplayItem item : cartItems) {
                    CreateOrderDetailDTO detail = new CreateOrderDetailDTO(
                            orderId,
                            item.getProduct().getProductId(),
                            item.getCartItem().getQuantity(),
                            item.getProduct().getPrice(),
                            item.getProduct().getPrice() * item.getCartItem().getQuantity()
                    );

                    orderRepository.createOrderDetail(detail).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) {
                                hasError[0] = true;
                                checkDone();
                                return;
                            }

                            cartRepository.deleteCartItem(item.getCartItem().getCartID()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (!response.isSuccessful()) hasError[0] = true;
                                    checkDone();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    hasError[0] = true;
                                    checkDone();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            hasError[0] = true;
                            checkDone();
                        }

                        private void checkDone() {
                            completed[0]++;
                            if (completed[0] == totalItems) {
                                orderSuccess.postValue(!hasError[0]);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ReadOrderDTO> call, Throwable t) {
                orderSuccess.postValue(false);
            }
        });
    }
}

