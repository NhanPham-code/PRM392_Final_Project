package com.example.bakeryshop.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bakeryshop.Adapters.CartAdapter;
import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.ViewModel.CartViewModel;
import com.example.bakeryshop.databinding.FragmentCartBinding;
import com.google.android.material.button.MaterialButton; // MỚI: Import MaterialButton

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener { // Triển khai interface

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private CartAdapter cartAdapter;

    // Danh sách để lưu trữ các CartDisplayItem đã được chọn để thanh toán
    private List<CartDisplayItem> selectedItemsForCheckout = new ArrayList<>();

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartAdapter = new CartAdapter();
        cartAdapter.setOnCartItemActionListener(this); // Thiết lập listener cho Adapter

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCart.setAdapter(cartAdapter);

        cartViewModel.cartItems.observe(getViewLifecycleOwner(), items -> {
            cartAdapter.setItems(items);
            // Sau khi danh sách giỏ hàng được cập nhật, đảm bảo selectedItemsForCheckout cũng được đồng bộ
            // Ví dụ: loại bỏ các item không còn tồn tại trong giỏ hàng, hoặc cập nhật lại trạng thái chọn
            // Đơn giản nhất là xóa và thêm lại các item đã chọn
            // (Tuy nhiên, cách tốt hơn là duyệt qua và giữ lại trạng thái đã chọn nếu item vẫn còn)
            // Tạm thời, để đơn giản, chúng ta sẽ làm lại logic chọn khi cần.
        });

        cartViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerCart.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        cartViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), "Thông báo: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        cartViewModel.fetchCartItems();

        // Xử lý sự kiện cho nút "Update Cart" (mới)
        binding.btnUpdateCart.setOnClickListener(v -> {
            cartViewModel.updateAllCartQuantities();
        });

        // Xử lý sự kiện cho nút "Thanh toán các món đã chọn" (mới)
        binding.btnCheckoutSelected.setOnClickListener(v -> {
            if (selectedItemsForCheckout.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm để thanh toán.", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Chuyển sang Activity thanh toán và truyền danh sách selectedItemsForCheckout đi
            // Ví dụ: Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            // intent.putExtra("selected_items", (ArrayList<CartDisplayItem>) selectedItemsForCheckout); // Cần CartDisplayItem implements Serializable hoặc Parcelable
            // startActivity(intent);
            Toast.makeText(getContext(), "Chuyển sang màn hình thanh toán với " + selectedItemsForCheckout.size() + " sản phẩm đã chọn.", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    //region Implement CartAdapter.OnCartItemActionListener
    @Override
    public void onIncreaseQuantity(CartDisplayItem item) {
        cartViewModel.updateQuantityLocally(item, 1);
    }

    @Override
    public void onDecreaseQuantity(CartDisplayItem item) {
        cartViewModel.updateQuantityLocally(item, -1);
    }

    @Override
    public void onDeleteItem(CartDisplayItem item) {
        cartViewModel.deleteCartItem(item);
    }

    @Override
    public void onItemSelected(CartDisplayItem item, boolean isChecked) {
        if (isChecked) {
            if (!selectedItemsForCheckout.contains(item)) {
                selectedItemsForCheckout.add(item);
            }
        } else {
            selectedItemsForCheckout.remove(item);
        }
        Log.d("CartFragment", "Selected items count: " + selectedItemsForCheckout.size());
        // Có thể cập nhật UI, ví dụ: kích hoạt/vô hiệu hóa nút thanh toán
        binding.btnCheckoutSelected.setEnabled(!selectedItemsForCheckout.isEmpty());
    }
    //endregion
}