package com.example.bakeryshop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.R;

import java.text.DecimalFormat;
import java.util.List;

public class CartDisplayItemAdapter extends RecyclerView.Adapter<CartDisplayItemAdapter.CartDisplayItemViewHolder> {

    private List<CartDisplayItem> items;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public CartDisplayItemAdapter(List<CartDisplayItem> items) {
        this.items = items;
    }

    public void setItems(List<CartDisplayItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartDisplayItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_product_summary, parent, false);
        return new CartDisplayItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartDisplayItemViewHolder holder, int position) {
        CartDisplayItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    class CartDisplayItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;

        public CartDisplayItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name_summary);
            tvQuantity = itemView.findViewById(R.id.tv_quantity_summary);
            tvPrice = itemView.findViewById(R.id.tv_price_summary);
        }

        public void bind(CartDisplayItem item) {
            tvProductName.setText(item.getProduct().getProductName());
            tvQuantity.setText("x" + item.getCartItem().getQuantity());
            double totalPrice = item.getCartItem().getQuantity() * item.getProduct().getPrice();
            tvPrice.setText(decimalFormat.format(totalPrice) + " VND");
        }
    }
}