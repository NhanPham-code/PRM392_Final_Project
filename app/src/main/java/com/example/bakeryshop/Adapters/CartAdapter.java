package com.example.bakeryshop.Adapters;

import android.graphics.Bitmap; // Giữ lại import này mặc dù không dùng HttpURLConnection nữa, có thể bạn dùng ở nơi khác
import android.graphics.BitmapFactory; // Giữ lại import này
import android.os.Handler;
import android.os.Looper;
import android.util.Log; // <--- THÊM DÒNG NÀY ĐỂ DEBUG
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // Giữ lại Toast cho logic tăng/giảm số lượng

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.R;
import com.google.android.material.imageview.ShapeableImageView; // Giữ lại ShapeableImageView

import java.io.InputStream; // Giữ lại import này
import java.net.HttpURLConnection; // Giữ lại import này
import java.net.URL; // Giữ lại import này
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService; // Giữ lại import này
import java.util.concurrent.Executors; // Giữ lại import này

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartDisplayItem> items = new ArrayList<>();
    private OnCartItemActionListener listener;
    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private final Map<Integer, Boolean> selectedStates = new HashMap<>();
    // Dù không dùng cho tải ảnh nữa, giữ lại ExecutorService và Handler vì có thể dùng cho tác vụ bất đồng bộ khác
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnCartItemActionListener {
        void onIncreaseQuantity(CartDisplayItem item);
        void onDecreaseQuantity(CartDisplayItem item);
        void onDeleteItem(CartDisplayItem item);
        void onItemSelected(CartDisplayItem item, boolean isChecked);
    }

    public void setOnCartItemActionListener(OnCartItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartDisplayItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);

        Map<Integer, Boolean> newSelectedStates = new HashMap<>();
        for (CartDisplayItem item : newItems) {
            int cartId = item.getCartItem().getCartID();
            newSelectedStates.put(cartId, selectedStates.getOrDefault(cartId, false));
            item.setSelected(newSelectedStates.get(cartId));
        }
        this.selectedStates.clear();
        this.selectedStates.putAll(newSelectedStates);
        notifyDataSetChanged();
    }

    public void clearSelections() {
        selectedStates.clear();
        for (CartDisplayItem item : items) {
            item.setSelected(false);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartDisplayItem item = items.get(position);
        holder.bind(item);

        holder.cbSelect.setOnCheckedChangeListener(null); // Tránh gọi listener khi tái sử dụng view
        holder.cbSelect.setChecked(item.isSelected());

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked); // Cập nhật trạng thái trong DTO
            selectedStates.put(item.getCartItem().getCartID(), isChecked); // Cập nhật trong Map
            if (listener != null) {
                listener.onItemSelected(item, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvQuantity;
        ImageButton ivDecreaseQuantity, ivIncreaseQuantity, ivDelete;
        CheckBox cbSelect;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.img_item);
            tvProductName = itemView.findViewById(R.id.tv_name);
            tvProductPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            ivDecreaseQuantity = itemView.findViewById(R.id.btn_decrease);
            ivIncreaseQuantity = itemView.findViewById(R.id.btn_increase);
            ivDelete = itemView.findViewById(R.id.btn_delete);
            cbSelect = itemView.findViewById(R.id.cb_select_item);
        }

        public void bind(CartDisplayItem item) {
            tvProductName.setText(item.getProduct().getProductName());
            tvProductPrice.setText(decimalFormat.format(item.getProduct().getPrice()) + " VND");
            tvQuantity.setText(String.valueOf(item.getCartItem().getQuantity()));

            // *************************************************************************
            // BẮT ĐẦU: Logic tải ảnh từ tài nguyên cục bộ (copy từ code cũ của bạn)
            String imgUrl = item.getProduct().getImageUrl();
            Log.d("ImageLoadDebug", "Loading local image for product: " + item.getProduct().getProductName() + ", raw URL from DTO: " + imgUrl);

            if (imgUrl != null && !imgUrl.isEmpty()) {
                String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
                String noExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                String resourceName = noExt.toLowerCase(); // Chuyển về chữ thường

                Log.d("ImageLoadDebug", "Searching for local resource name: " + resourceName + " in package: " + itemView.getContext().getPackageName());

                int resId = itemView.getContext().getResources().getIdentifier(
                        resourceName,   // Tên tài nguyên (ví dụ: "image_bb50e1")
                        "drawable",     // Loại tài nguyên (kiểm tra lại thư mục ảnh của bạn là drawable hay mipmap)
                        itemView.getContext().getPackageName()); // Package của ứng dụng

                if (resId != 0) {
                    ivProductImage.setImageResource(resId);
                    Log.d("ImageLoadDebug", "Successfully set local image for product: " + item.getProduct().getProductName());
                } else {
                    ivProductImage.setImageResource(R.drawable.bunny); // Ảnh mặc định nếu không tìm thấy
                    Log.e("ImageLoadDebug", "Local resource ID NOT FOUND for name: " + resourceName + ". Using default.");
                }
            } else {
                ivProductImage.setImageResource(R.drawable.bunny); // Ảnh mặc định nếu URL từ DTO rỗng/null
                Log.w("ImageLoadDebug", "Image URL from DTO is null or empty for product: " + item.getProduct().getProductName() + ". Using default.");
            }
            // KẾT THÚC: Logic tải ảnh từ tài nguyên cục bộ
            // *************************************************************************

            ivIncreaseQuantity.setOnClickListener(v -> {
                if (listener != null) listener.onIncreaseQuantity(item);
            });
            ivDecreaseQuantity.setOnClickListener(v -> {
                if (listener != null) listener.onDecreaseQuantity(item);
            });
            ivDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteItem(item);
            });

            // Logic kiểm tra stockQuantity
            if (item.getCartItem().getQuantity() >= item.getProduct().getQuantity()) {
                ivIncreaseQuantity.setEnabled(false);
                ivIncreaseQuantity.setAlpha(0.5f);
            } else {
                ivIncreaseQuantity.setEnabled(true);
                ivIncreaseQuantity.setAlpha(1.0f);
            }
            if (item.getCartItem().getQuantity() <= 1) {
                ivDecreaseQuantity.setEnabled(false);
                ivDecreaseQuantity.setAlpha(0.5f);
            } else {
                ivDecreaseQuantity.setEnabled(true);
                ivDecreaseQuantity.setAlpha(1.0f);
            }
        }
    }
}