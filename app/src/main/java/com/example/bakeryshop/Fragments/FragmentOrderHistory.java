package com.example.bakeryshop.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bakeryshop.Adapters.OrderAdapter;
import com.example.bakeryshop.R;
import com.example.bakeryshop.ViewModel.OrderHistoryViewModel;
import com.example.bakeryshop.databinding.FragmentOrderHistoryBinding;

public class FragmentOrderHistory extends Fragment {

    private FragmentOrderHistoryBinding binding;
    private OrderHistoryViewModel orderHistoryViewModel;
    private OrderAdapter orderAdapter;

    public FragmentOrderHistory() {
        // Empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false);

        // Khởi tạo ViewModel
        orderHistoryViewModel = new OrderHistoryViewModel(requireActivity().getApplication());

        // Thiết lập RecyclerView
        orderAdapter = new OrderAdapter(new java.util.ArrayList<>(), order -> {
            OrderHistoryDetailFragment fragment = new OrderHistoryDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("order", order); // orderDTO là ReadOrderDTO bạn muốn truyền
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

        });

        binding.recyclerOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerOrderList.setAdapter(orderAdapter);

        // Quan sát dữ liệu đơn hàng
        orderHistoryViewModel._orderHistory.observe(getViewLifecycleOwner(), orders -> {
            orderAdapter.setOrderList(orders);
        });

        // Quan sát trạng thái loading
        orderHistoryViewModel._isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.recyclerOrderList.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Quan sát lỗi nếu có
        orderHistoryViewModel._errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Gọi API để lấy đơn hàng
        orderHistoryViewModel.fetchOrderHistory();

        return binding.getRoot();
    }
}