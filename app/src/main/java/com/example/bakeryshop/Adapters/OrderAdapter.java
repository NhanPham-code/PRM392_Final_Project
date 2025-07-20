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
import com.example.bakeryshop.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<ReadOrderDTO> orderList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ReadOrderDTO order);
    }

    public OrderAdapter(List<ReadOrderDTO> orderList, OnItemClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Adapter", "→ onCreateViewHolder đang chạy");  // ✅ LOG
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_order, parent, false); // ✅ Đúng tên layout
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ReadOrderDTO order = orderList.get(position);
        holder.bind(order, listener);
        Log.d("Adapter", "→ Đang bind đơn: " + order.getOrderStatus()); // ✅ LOG
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public void setOrderList(List<ReadOrderDTO> orderList) {
        this.orderList = orderList;
        Log.d("Adapter", "→ Gán danh sách: " + orderList.size()); // ✅ LOG
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

        public void bind(ReadOrderDTO order, OnItemClickListener listener) {
            if (order == null) return;

            tvPrice.setText(formatPrice(order.getTotalAmount()));
            tvOrderDate.setText("Ngày đặt: " + order.getOrderDate());
            tvStatus.setText(order.getOrderStatus());
            tvPaymentMethod.setText("Thanh toán: " + order.getPaymentMethod());
            tvShippingAddress.setText("Giao đến: " + order.getShippingAddress());

            // Tô màu trạng thái
            if ("Shipped".equalsIgnoreCase(order.getOrderStatus())) {
                tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Xanh lá
            } else if("Pending".equalsIgnoreCase(order.getOrderStatus())) {
                tvStatus.setTextColor(Color.parseColor("#FF9800")); // Cam
            }

            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }

        private String formatPrice(double price) {
            return String.format("₫%,.0f", price);
        }
    }
}