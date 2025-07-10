package com.example.bakeryshop.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartDisplayItem> items = new ArrayList<>();
    private OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onIncreaseQuantity(CartDisplayItem item);
        void onDecreaseQuantity(CartDisplayItem item);
        void onDeleteItem(CartDisplayItem item);
        void onItemSelected(CartDisplayItem item, boolean isChecked);
    }

    public void setOnCartItemActionListener(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartDisplayItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, listener, items); // Truyền list items vào ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvPrice, tvQuantity;
        private final ImageView imgProduct;
        private final ImageButton btnIncrease, btnDecrease, btnDelete;
        private final CheckBox cbSelectItem;

        private OnCartItemActionListener listener;
        private List<CartDisplayItem> adapterItems; // Giữ tham chiếu đến list của adapter

        public CartViewHolder(@NonNull View itemView, OnCartItemActionListener listener, List<CartDisplayItem> adapterItems) {
            super(itemView);
            this.listener = listener;
            this.adapterItems = adapterItems; // Nhận list items
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            imgProduct = itemView.findViewById(R.id.img_item);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            cbSelectItem = itemView.findViewById(R.id.cb_select_item);

            setupListeners(); // Chỉ gọi một lần trong constructor
        }

        private void setupListeners() {
            btnIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) { // Đảm bảo position hợp lệ
                        CartDisplayItem currentItem = adapterItems.get(position);
                        // Cập nhật quantity cục bộ trước để UI thay đổi ngay lập tức
                        int currentQuantity = currentItem.getCartItem().getQuantity();
                        int stockQuantity = currentItem.getProduct().getQuantity(); // Lấy stockQuantity

                        if (currentQuantity < stockQuantity) { // Chỉ tăng nếu chưa đạt giới hạn stock
                            listener.onIncreaseQuantity(currentItem); // Gọi listener để ViewModel xử lý
                            currentItem.getCartItem().setQuantity(currentQuantity + 1); // Cập nhật tạm thời
                            tvQuantity.setText(String.valueOf(currentQuantity + 1)); // Cập nhật UI
                        } else {
                            Toast.makeText(itemView.getContext(), "Số lượng đã đạt giới hạn tồn kho: " + stockQuantity, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            btnDecrease.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CartDisplayItem currentItem = adapterItems.get(position);
                        int currentQuantity = currentItem.getCartItem().getQuantity();

                        if (currentQuantity > 1) { // Chỉ giảm nếu lớn hơn 1
                            listener.onDecreaseQuantity(currentItem); // Gọi listener để ViewModel xử lý
                            currentItem.getCartItem().setQuantity(currentQuantity - 1); // Cập nhật tạm thời
                            tvQuantity.setText(String.valueOf(currentQuantity - 1)); // Cập nhật UI
                        } else {
                            Toast.makeText(itemView.getContext(), "Số lượng không thể nhỏ hơn 1.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteItem(adapterItems.get(position));
                    }
                }
            });

            cbSelectItem.setOnCheckedChangeListener(null); // Xóa listener cũ trước khi gán mới
            cbSelectItem.setChecked(false); // Reset trạng thái để tránh gọi listener khi recycle

            // Đặt listener mới
            cbSelectItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        CartDisplayItem currentItem = adapterItems.get(position);
                        currentItem.setSelected(isChecked);
                        listener.onItemSelected(currentItem, isChecked);
                    }
                }
            });
        }

        public void bind(CartDisplayItem item) {
            // Không cần setOnClickListener ở đây nữa!
            // Chỉ cần cập nhật UI
            tvName.setText(item.getProduct().getProductName());

            DecimalFormat format = new DecimalFormat("#,##0₫");
            tvPrice.setText(format.format(item.getProduct().getPrice()));

            tvQuantity.setText(String.valueOf(item.getCartItem().getQuantity()));

            // Quan trọng: Đặt trạng thái checkbox mà không kích hoạt listener để tránh vòng lặp
            cbSelectItem.setOnCheckedChangeListener(null); // Tắt listener tạm thời
            cbSelectItem.setChecked(item.isSelected());
            setupListeners(); // Gán lại listener sau khi đã setChecked
            // Hoặc bạn có thể chỉ gán lại OnCheckedChangeListener()
            // `cbSelectItem.setOnCheckedChangeListener(listenerForCheckbox);`
            // (cách này tốt hơn)

            // Load ảnh
            String imgUrl = item.getProduct().getImageUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
                String noExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                int resId = itemView.getContext().getResources().getIdentifier(
                        noExt.toLowerCase(), "drawable", itemView.getContext().getPackageName());

                if (resId != 0) {
                    imgProduct.setImageResource(resId);
                } else {
                    imgProduct.setImageResource(R.drawable.placeholder);
                }
            } else {
                imgProduct.setImageResource(R.drawable.placeholder);
            }
        }
    }
}