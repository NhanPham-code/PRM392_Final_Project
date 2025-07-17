package com.example.bakeryshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Adapters.CartDisplayItemAdapter;
import com.example.bakeryshop.Data.DTO.CartDisplayItem;
import com.example.bakeryshop.ViewModel.CheckoutViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalAmount;
    private TextView tvCheckoutTitle;
    private TextInputEditText etShippingAddress;
    private RadioGroup rgPaymentMethod;
    private MaterialButton btnPlaceOrder;
    private MaterialButton btnSelectLocation;

    private CartDisplayItemAdapter adapter;
    private List<CartDisplayItem> selectedItems = new ArrayList<>();
    private CheckoutViewModel viewModel;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    // Activity result launcher cho MapActivity
    private ActivityResultLauncher<Intent> mapActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Khởi tạo views
        recyclerView = findViewById(R.id.recycler_checkout_items);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvCheckoutTitle = findViewById(R.id.tv_checkout_title);
        etShippingAddress = findViewById(R.id.et_shipping_address);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnSelectLocation = findViewById(R.id.btn_select_location);

        viewModel = new CheckoutViewModel(getApplicationContext());

        // Khởi tạo activity result launcher
        mapActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String selectedAddress = result.getData().getStringExtra("selected_address");
                        if (selectedAddress != null && !selectedAddress.isEmpty()) {
                            etShippingAddress.setText(selectedAddress);
                            Toast.makeText(this, "Đã chọn địa chỉ thành công", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Nhận danh sách từ intent
        selectedItems = (List<CartDisplayItem>) getIntent().getSerializableExtra("selected_items");
        if (selectedItems == null) selectedItems = new ArrayList<>();

        adapter = new CartDisplayItemAdapter(selectedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotalAmount();
        setupClickListeners();

        viewModel.getOrderSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Quay về
            } else {
                Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Click vào title để chuyển sang MapActivity
        tvCheckoutTitle.setOnClickListener(v -> openMapActivity());

        // Click vào nút chọn vị trí
        btnSelectLocation.setOnClickListener(v -> openMapActivity());

        // Click vào nút đặt hàng
        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());
    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        // Truyền địa chỉ hiện tại (nếu có) để hiển thị trên map
        String currentAddress = etShippingAddress.getText().toString().trim();
        if (!currentAddress.isEmpty()) {
            intent.putExtra("current_address", currentAddress);
        }
        mapActivityLauncher.launch(intent);
    }

    private void updateTotalAmount() {
        double total = 0;
        for (CartDisplayItem item : selectedItems) {
            total += item.getProduct().getPrice() * item.getCartItem().getQuantity();
        }
        tvTotalAmount.setText("Tổng tiền: " + decimalFormat.format(total) + " VND");
    }

    private void handlePlaceOrder() {
        String address = etShippingAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (CartDisplayItem item : selectedItems) {
            total += item.getProduct().getPrice() * item.getCartItem().getQuantity();
        }

        int selectedRadioId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioId);
        String paymentMethod = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "COD";

        // Gửi lên ViewModel
        viewModel.placeOrder(paymentMethod, address, total, selectedItems);
    }
}