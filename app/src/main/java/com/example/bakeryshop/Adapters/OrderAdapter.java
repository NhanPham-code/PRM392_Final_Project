package com.example.bakeryshop.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.ReadOrderDTO;
import com.example.bakeryshop.Data.DTO.ReadOrderHistoryDTO;
import com.example.bakeryshop.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<ReadOrderHistoryDTO> orderList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ReadOrderHistoryDTO order);
    }

    public OrderAdapter(List<ReadOrderHistoryDTO> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "→ onCreateViewHolder is running");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ReadOrderHistoryDTO order = orderList.get(position);
        holder.bind(order, listener);
        Log.d("Adapter", "→ Binding order: " + order.getOrderStatus());
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void setOrderList(List<ReadOrderHistoryDTO> orderList) {
        this.orderList = orderList;
        Log.d("Adapter", "→ Set order list: " + orderList.size());
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvPrice, tvOrderDate, tvStatus, tvPaymentMethod, tvShippingAddress;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvStatus = itemView.findViewById(R.id.status);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            tvShippingAddress = itemView.findViewById(R.id.tv_shipping_address);
        }

        public void bind(ReadOrderHistoryDTO order, OnItemClickListener listener) {
            if (order == null) return;

            tvPrice.setText(formatPrice(order.getTotalAmount()));
            tvOrderDate.setText("Order Date: " + order.getOrderDate());
            tvStatus.setText(order.getOrderStatus());
            tvPaymentMethod.setText("Payment: " + order.getPaymentMethod());
            tvShippingAddress.setText("Deliver to: " + order.getShippingAddress());

            // Set status color
            if ("Shipped".equalsIgnoreCase(order.getOrderStatus()) ||
                    "Delivered".equalsIgnoreCase(order.getOrderStatus())) {
                tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else if ("Pending".equalsIgnoreCase(order.getOrderStatus())) {
                tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
            } else if ("Processing".equalsIgnoreCase(order.getOrderStatus())) {
                tvStatus.setTextColor(Color.parseColor("#2196F3")); // Blue
            } else if ("Cancelled".equalsIgnoreCase(order.getOrderStatus())) {
                tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
            }

            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }

        private String formatPrice(double price) {
            return String.format("$%.2f", price);
        }
    }
}