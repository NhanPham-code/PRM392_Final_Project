package com.example.bakeryshop.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bakeryshop.Adapters.OrderDetailAdapter;
import com.example.bakeryshop.Data.DTO.ReadOrderDTO;
import com.example.bakeryshop.R;
import com.example.bakeryshop.ViewModel.OrderHistoryDetailViewModel;
import com.example.bakeryshop.databinding.FragmentOrderHistoryDetailBinding;

import java.util.ArrayList;

public class OrderHistoryDetailFragment extends Fragment {

    private FragmentOrderHistoryDetailBinding binding;
    private OrderDetailAdapter adapter;
    private OrderHistoryDetailViewModel viewModel;

    public OrderHistoryDetailFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderHistoryDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar Back
        binding.toolbar.setNavigationOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Lấy order từ arguments
        ReadOrderDTO order = (ReadOrderDTO) getArguments().getSerializable("order");
        if (order == null) {
            Toast.makeText(getContext(), "Không có dữ liệu đơn hàng!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        // ViewModelProvider CHUẨN
        viewModel = new ViewModelProvider(
                this,
                new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
        ).get(OrderHistoryDetailViewModel.class);

        // Adapter
        adapter = new OrderDetailAdapter(requireContext(), new ArrayList<>());
        binding.recyclerOrderList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerOrderList.setAdapter(adapter);

        // Observe data
        viewModel._orderDetails.observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                adapter.setOrderDetailList(orders);
                Log.d("OrderDetail", "✅ Số item: " + orders.size());
            }
        });

        // Observe loading
        viewModel._isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.recyclerOrderList.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            }
        });

        // Observe error
        viewModel._errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Gọi API
        viewModel.fetchOrderDetails(order.orderID);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
