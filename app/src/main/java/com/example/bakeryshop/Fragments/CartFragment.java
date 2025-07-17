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
import com.example.bakeryshop.CheckoutActivity;
import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.ViewModel.CartViewModel;
import com.example.bakeryshop.databinding.FragmentCartBinding; // Đảm bảo bạn đã enable ViewBinding trong build.gradle

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemActionListener {

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
        cartAdapter.setOnCartItemActionListener(this);

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCart.setAdapter(cartAdapter);

        // Quan sát dữ liệu giỏ hàng từ ViewModel
        cartViewModel.cartItems.observe(getViewLifecycleOwner(), items -> {
            cartAdapter.setItems(items);
            // Sau khi danh sách giỏ hàng được cập nhật, cập nhật lại selectedItemsForCheckout
            // dựa trên trạng thái isSelected của các item mới.
            updateSelectedItemsList(items);
            binding.btnCheckoutSelected.setEnabled(!selectedItemsForCheckout.isEmpty()); // Cập nhật trạng thái nút
        });

        // Quan sát trạng thái loading
        cartViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.recyclerCart.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        });

        // Quan sát lỗi
        cartViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), "Thông báo: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        // Observer cho các thông báo thành công từ ViewModel (nếu có)
        cartViewModel.deleteSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null) {
                if (isSuccess) {
                    Toast.makeText(getContext(), "Xóa sản phẩm khỏi giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                }
                cartViewModel.resetDeleteStatus();
            }
        });

        cartViewModel.updateSuccess.observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess != null) {
                if (isSuccess) {
                    Toast.makeText(getContext(), "Cập nhật giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                }
                cartViewModel.resetUpdateStatus();
            }
        });

        // Fetch giỏ hàng lần đầu
        cartViewModel.fetchCartItems();

        binding.btnUpdateCart.setOnClickListener(v -> {
            cartViewModel.updateAllCartQuantities();
        });

        binding.btnCheckoutSelected.setOnClickListener(v -> {
            if (selectedItemsForCheckout.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm để thanh toán.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            intent.putExtra("selected_items", new ArrayList<>(selectedItemsForCheckout)); // An toàn hơn
            startActivity(intent);
        });

        // Ban đầu vô hiệu hóa nút thanh toán nếu không có gì được chọn
        binding.btnCheckoutSelected.setEnabled(false);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cần fetch lại giỏ hàng khi quay lại từ CheckoutAcivity
        // (để cập nhật nếu các mặt hàng đã được đặt hàng và xóa khỏi giỏ)
        cartViewModel.fetchCartItems();
        // Reset danh sách chọn và trạng thái checkbox trong adapter
        selectedItemsForCheckout.clear(); // Xóa danh sách đã chọn
        binding.btnCheckoutSelected.setEnabled(false); // Vô hiệu hóa nút
        if (cartAdapter != null) {
            cartAdapter.clearSelections(); // Đảm bảo phương thức này cập nhật cả trạng thái isSelected trong DTO
        }
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
        binding.btnCheckoutSelected.setEnabled(!selectedItemsForCheckout.isEmpty()); // Kích hoạt/vô hiệu hóa nút
    }
    //endregion

    // Phương thức trợ giúp để cập nhật danh sách selectedItemsForCheckout
    private void updateSelectedItemsList(List<CartDisplayItem> currentCartItems) {
        List<CartDisplayItem> newSelectedItems = new ArrayList<>();
        for (CartDisplayItem item : currentCartItems) {
            if (item.isSelected()) {
                newSelectedItems.add(item);
            }
        }
        this.selectedItemsForCheckout = newSelectedItems;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Xóa tham chiếu binding khi view bị hủy
    }
}