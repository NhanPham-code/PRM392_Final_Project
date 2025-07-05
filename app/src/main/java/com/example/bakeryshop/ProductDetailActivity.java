package com.example.bakeryshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log class
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Adapters.ProductAdapter;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.ViewModel.ProductDetailViewModel;
import com.example.bakeryshop.ViewModel.ProductListViewModel;
import com.example.bakeryshop.databinding.ActivityProductDetailBinding;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ReadProductDTO productDTO;
    private ProductListViewModel productListViewModel; // Đổi tên biến

    private static final String TAG = "ProductDetailActivity"; // TAG cho logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        productListViewModel = new ViewModelProvider(this).get(ProductListViewModel.class);

        // Khởi tạo View Binding trước
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Xử lý insets để tránh giao diện bị che bởi thanh hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy ProductId từ Intent
        productDTO = (ReadProductDTO) getIntent().getSerializableExtra("product");
        int resourceId = getIntent().getIntExtra("resourceId", 0);

        // Kiểm tra ProductId hợp lệ
        if (productDTO.getProductId() == 0) {
            Log.e(TAG, "Không nhận được ProductId hợp lệ từ Intent. Đang đóng Activity.");
            Toast.makeText(this, "Không thể tải chi tiết sản phẩm: ID không hợp lệ.", Toast.LENGTH_LONG).show();
            finish(); // Đóng Activity nếu không có ID hợp lệ
            return; // Dừng các hoạt động tiếp theo
        } else {
            Log.d(TAG, "Đã nhận Product ID từ Intent: " + productDTO.getProductId());
        }

            if (resourceId != 0) {
                binding.mainProductImage.setImageResource(resourceId);
            } else {
                Log.w(TAG, "Không tìm thấy drawable với tên: " + resourceId + ". Đang sử dụng ảnh placeholder.");
                binding.mainProductImage.setImageResource(R.drawable.placeholder);
            }
            binding.detailProductName.setText(productDTO.getProductName());
            binding.detailProductPrice.setText(productDTO.getPrice() + "$");
            binding.totalPriceTextView.setText(productDTO.getPrice() + "$");
            binding.detailProductDescription.setText(productDTO.getDescription());
            binding.detailProductQuantity.setText(String.valueOf(productDTO.getQuantity()));
            binding.quantityTextView.setText("1");

        // --- Thêm sự kiện click cho nút backButton ---
        binding.backButton.setOnClickListener(v -> {
            // Cách 1: Mô phỏng nút back của hệ thống
            onBackPressed();

            // Hoặc Cách 2: Quay về MainActivity một cách rõ ràng
            // Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
            // // Bạn có thể thêm cờ nếu cần xóa các Activity khác khỏi stack
            // // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            // startActivity(intent);
            // finish(); // Đóng ProductDetailActivity sau khi chuyển hướng
        });

        binding.increaseQuantityButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(binding.quantityTextView.getText().toString());
            if(productDTO.getQuantity() > currentQuantity){
                binding.quantityTextView.setText(String.valueOf(currentQuantity + 1));
                binding.totalPriceTextView.setText(String.format("%.2f", productDTO.getPrice() * (currentQuantity + 1)) + "$");
            }
        });

        binding.decreaseQuantityButton.setOnClickListener(v -> {
           int currentQuantity = Integer.parseInt(binding.quantityTextView.getText().toString());
           if (currentQuantity > 1) {
               binding.quantityTextView.setText(String.valueOf(currentQuantity - 1));
               binding.totalPriceTextView.setText(String.format("%.2f", productDTO.getPrice() * (currentQuantity - 1)) + "$");
           }
        });

        ProductAdapter productAdapter = new ProductAdapter();
        binding.recommentItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recommentItemsRecyclerView.setAdapter(productAdapter);

        int id = productDTO.getCategoryId();

        productListViewModel.fetchProductsByCategoryId(productDTO.getCategoryId());

        productListViewModel.products.observe(this, products -> {



            productAdapter.setProductList(products);
        });



        productListViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                binding.detailProgressBar.setVisibility(View.VISIBLE);
            } else {
                binding.detailProgressBar.setVisibility(View.GONE);
            }
        });

        productListViewModel.errorMessage.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }


}