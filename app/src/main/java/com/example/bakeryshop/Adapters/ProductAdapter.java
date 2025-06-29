package com.example.bakeryshop.Adapters;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.R;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ReadProductDTO> productList;

    public ProductAdapter() {
    }

    public void setProductList(List<ReadProductDTO> productList) {
        this.productList = productList;
        notifyDataSetChanged(); // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout and create ViewHolder instance
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Bind data to the ViewHolder
        ReadProductDTO product = productList.get(position);
        // Use the bind method to set data
        holder.bind(product); // Use the bind method to set data
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0; // Replace with actual item count
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Define your ViewHolder views here
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;

        public ProductViewHolder(View itemView) {
            super(itemView);
            // Initialize your views
            productImage = itemView.findViewById(R.id.imgProduct);
            productName = itemView.findViewById(R.id.txtProductName);
            productPrice = itemView.findViewById(R.id.txtProductPrice);
        }

        // Phương thức để bind dữ liệu ReadProductDTO vào các View
        public void bind(ReadProductDTO product) { // Sử dụng ReadProductDTO
            productName.setText(product.getProductName());

            DecimalFormat formatter = new DecimalFormat("#,##0₫"); // Định dạng giá
            productPrice.setText(formatter.format(product.getPrice()));

            // Lấy tên file ảnh từ URL (ví dụ: "img/product/savory/chocolatecroissant.png" -> "chocolatecroissant")
            String imageUrl = product.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fileNameWithExtension = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                String fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
                String drawableName = fileName.toLowerCase(); // Tên drawable thường là chữ thường

                // Lấy ID tài nguyên drawable từ tên sử dụng context của itemView
                int resourceId = itemView.getContext().getResources().getIdentifier(
                        drawableName, "drawable", itemView.getContext().getPackageName());

                if (resourceId != 0) {
                    productImage.setImageResource(resourceId);
                } else {
                    // Nếu không tìm thấy drawable, đặt ảnh placeholder
                    productImage.setImageResource(R.drawable.placeholder);
                }
            } else {
                // Nếu URL rỗng hoặc null, đặt ảnh placeholder
                productImage.setImageResource(R.drawable.placeholder);
            }

            // Xử lý sự kiện click trên item (tùy chọn)
            itemView.setOnClickListener(v -> {
                // TODO: Xử lý sự kiện khi click vào item sản phẩm
                Toast.makeText(itemView.getContext(), "Clicked product: " + product.getProductName(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
