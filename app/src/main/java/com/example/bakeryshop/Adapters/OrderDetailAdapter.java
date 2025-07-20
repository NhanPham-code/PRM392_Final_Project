package com.example.bakeryshop.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.ReadOrderDetailDTO;
import com.example.bakeryshop.R;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private List<ReadOrderDetailDTO> orderDetailList;
    private final Context context;

    public OrderDetailAdapter(Context context, List<ReadOrderDetailDTO> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
    }

    public void setOrderDetailList(List<ReadOrderDetailDTO> newOrderDetailList) {
        this.orderDetailList = newOrderDetailList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail_history, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        ReadOrderDetailDTO currentItem = orderDetailList.get(position);

        holder.productNameTextView.setText(currentItem.productName);
        holder.quantityTextView.setText("Quantity: " + currentItem.quantity);
        holder.unitPriceTextView.setText(String.format("Unit Price: %.2f", currentItem.unitPrice));
        holder.totalPriceTextView.setText(String.format("Total: %.2f", currentItem.getTotalPrice()));

        // Debug log
        Log.d("Adapter", "Bind: " + currentItem.productName + " | Img: " + currentItem.productImg);

        String imageUrl = currentItem.getProductImg();
        ImageView productImage = holder.productImageView;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String fileNameWithExtension = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                String fileName = fileNameWithExtension.contains(".")
                        ? fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'))
                        : fileNameWithExtension;

                String drawableName = fileName.toLowerCase();
                int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                        drawableName, "drawable", holder.itemView.getContext().getPackageName());

                if (resourceId != 0) {
                    productImage.setImageResource(resourceId);
                } else {
                    productImage.setImageResource(R.drawable.placeholder);
                }
            } catch (Exception e) {
                Log.e("Adapter", "Error processing image: " + e.getMessage());
                productImage.setImageResource(R.drawable.placeholder);
            }
        } else {
            productImage.setImageResource(R.drawable.placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList == null ? 0 : orderDetailList.size();
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productNameTextView;
        TextView quantityTextView;
        TextView unitPriceTextView;
        TextView totalPriceTextView;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.iv_product_img);
            productNameTextView = itemView.findViewById(R.id.tv_product_name);
            quantityTextView = itemView.findViewById(R.id.tv_quantity);
            unitPriceTextView = itemView.findViewById(R.id.tv_unit_price);
            totalPriceTextView = itemView.findViewById(R.id.tv_total_price);
        }
    }
}
