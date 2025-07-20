package com.example.bakeryshop.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.ReadOrderDetailDTO;
import com.example.bakeryshop.Data.Repository.ProductRepository;
import com.example.bakeryshop.R;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<ReadOrderDetailDTO> orderDetailList;
    private ProductRepository productRepository;
    private Context context;
    public OrderDetailAdapter(List<ReadOrderDetailDTO> orderDetailList, Context context) {
        this.orderDetailList = orderDetailList;
        this.context = context;
    }


    @NonNull
    @Override
    public OrderDetailAdapter.OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail_history, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.OrderDetailViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
