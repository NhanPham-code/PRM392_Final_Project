package com.example.bakeryshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bakeryshop.Adapters.ProductAdapter;
import com.example.bakeryshop.Data.DTO.ReadProductDTO;
import com.example.bakeryshop.ViewModel.ProductDetailViewModel; // Đã đổi ViewModel
import com.example.bakeryshop.databinding.ActivityProductDetailBinding;

import java.text.DecimalFormat; // Import để định dạng tiền tệ
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel productDetailViewModel; // Đã đổi ViewModel
    private ReadProductDTO currentProduct; // Đổi tên biến để rõ ràng hơn

    private static final String TAG = "ProductDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        productDetailViewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class); // Khởi tạo ProductDetailViewModel

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Lấy ProductDTO từ Intent
        currentProduct = (ReadProductDTO) getIntent().getSerializableExtra("product");
        int resourceId = getIntent().getIntExtra("resourceId", 0);

        if (currentProduct == null || currentProduct.getProductId() == 0) {
            Log.e(TAG, "Không nhận được ProductDTO hợp lệ từ Intent. Đang đóng Activity.");
            Toast.makeText(this, "Không thể tải chi tiết sản phẩm: Dữ liệu không hợp lệ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            Log.d(TAG, "Đã nhận Product ID từ Intent: " + currentProduct.getProductId());
        }

        // --- Hiển thị thông tin sản phẩm ---
        if (resourceId != 0) {
            binding.mainProductImage.setImageResource(resourceId);
        } else {
            String imgUrl = currentProduct.getImageUrl();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
                String noExt = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                int resId = getResources().getIdentifier(
                        noExt.toLowerCase(), "drawable", getPackageName());

                if (resId != 0) {
                    binding.mainProductImage.setImageResource(resId);
                } else {
                    binding.mainProductImage.setImageResource(R.drawable.placeholder);
                }
            } else {
                binding.mainProductImage.setImageResource(R.drawable.placeholder);
            }
        }
        binding.detailProductName.setText(currentProduct.getProductName());

        // Định dạng giá tiền
        DecimalFormat priceFormat = new DecimalFormat("#,##0₫");
        binding.detailProductPrice.setText(priceFormat.format(currentProduct.getPrice()));
        binding.totalPriceTextView.setText(priceFormat.format(currentProduct.getPrice())); // Giá ban đầu khi quantity = 1

        binding.detailProductDescription.setText(currentProduct.getDescription());
        binding.detailProductQuantity.setText(String.valueOf(currentProduct.getQuantity())); // Hiển thị số lượng tồn kho

        // Số lượng mặc định là 1 khi mới vào chi tiết sản phẩm
        binding.quantityTextView.setText("1");


        // --- Thêm sự kiện click cho nút backButton ---
        binding.backButton.setOnClickListener(v -> onBackPressed());

        // --- Xử lý tăng giảm số lượng ---
        binding.increaseQuantityButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(binding.quantityTextView.getText().toString());
            // Giới hạn tăng số lượng bằng stockQuantity của sản phẩm
            if (currentQuantity < currentProduct.getQuantity()) {
                currentQuantity++;
                binding.quantityTextView.setText(String.valueOf(currentQuantity));
                binding.totalPriceTextView.setText(priceFormat.format(currentProduct.getPrice() * currentQuantity));
            } else {
                Toast.makeText(this, "Số lượng đã đạt giới hạn tồn kho: " + currentProduct.getQuantity(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.decreaseQuantityButton.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(binding.quantityTextView.getText().toString());
            // Giới hạn giảm số lượng không dưới 1
            if (currentQuantity > 1) {
                currentQuantity--;
                binding.quantityTextView.setText(String.valueOf(currentQuantity));
                binding.totalPriceTextView.setText(priceFormat.format(currentProduct.getPrice() * currentQuantity));
            } else {
                Toast.makeText(this, "Số lượng không thể nhỏ hơn 1.", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Xử lý nút "Add to Cart" (MỚI) ---
        binding.addToCartButton.setOnClickListener(v -> {
            int productId = currentProduct.getProductId();
            int quantity = Integer.parseInt(binding.quantityTextView.getText().toString());
            productDetailViewModel.addItemToCart(productId, quantity);
        });

        // --- Quan sát trạng thái thêm vào giỏ hàng từ ViewModel ---
        productDetailViewModel.addToCartSuccess.observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(ProductDetailActivity.this, "Đã thêm sản phẩm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    // Tùy chọn: quay về màn hình trước hoặc cập nhật giỏ hàng
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Không thể thêm sản phẩm vào giỏ hàng.", Toast.LENGTH_SHORT).show();
                }
                productDetailViewModel.resetAddToCartStatus(); // Reset trạng thái để tránh Toast lặp lại
            }
        });

        productDetailViewModel.addToCartError.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(ProductDetailActivity.this, "Lỗi khi thêm vào giỏ hàng: " + errorMessage, Toast.LENGTH_LONG).show();
                productDetailViewModel.resetAddToCartStatus(); // Reset trạng thái
            }
        });

        // --- Xử lý RecyclerView gợi ý (giữ nguyên logic fetchProductsByCategoryId) ---
        ProductAdapter productAdapter = new ProductAdapter();
        binding.recommentItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recommentItemsRecyclerView.setAdapter(productAdapter);

        productDetailViewModel.fetchProductsByCategoryId(currentProduct.getCategoryId()); // Sử dụng productDetailViewModel

        productDetailViewModel.products.observe(this, products -> { // products LiveData từ ProductDetailViewModel
            // Lọc ra sản phẩm hiện tại để không hiển thị trong danh sách gợi ý
            if (products != null) {
                List<ReadProductDTO> recommendedProducts = new ArrayList<>();
                for (ReadProductDTO product : products) {
                    if (product.getProductId() != currentProduct.getProductId()) {
                        recommendedProducts.add(product);
                    }
                }
                productAdapter.setProductList(recommendedProducts);
            }
        });

        productDetailViewModel.productLoading.observe(this, isLoading -> { // productLoading từ ProductDetailViewModel
            binding.detailProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        productDetailViewModel.productError.observe(this, errorMessage -> { // productError từ ProductDetailViewModel
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}