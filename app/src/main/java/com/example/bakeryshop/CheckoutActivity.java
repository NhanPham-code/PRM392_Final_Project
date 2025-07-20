package com.example.bakeryshop;

import android.content.Intent;
import android.os.Bundle;
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
    private TextView tvTotalAmount, tvCheckoutTitle;
    private TextInputEditText etShippingAddress;
    private RadioGroup rgPaymentMethod;
    private MaterialButton btnPlaceOrder, btnSelectLocation;

    private CartDisplayItemAdapter adapter;
    private List<CartDisplayItem> selectedItems = new ArrayList<>();
    private CheckoutViewModel viewModel;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private ActivityResultLauncher<Intent> mapActivityLauncher;
    private ActivityResultLauncher<Intent> vnPayLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Init Views
        recyclerView = findViewById(R.id.recycler_checkout_items);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvCheckoutTitle = findViewById(R.id.tv_checkout_title);
        etShippingAddress = findViewById(R.id.et_shipping_address);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnSelectLocation = findViewById(R.id.btn_select_location);

        viewModel = new CheckoutViewModel(getApplicationContext());

        // Get selected items
        selectedItems = (List<CartDisplayItem>) getIntent().getSerializableExtra("selected_items");
        if (selectedItems == null) selectedItems = new ArrayList<>();

        adapter = new CartDisplayItemAdapter(selectedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotalAmount();

        // MapActivity launcher
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

        // VNPay launcher
        vnPayLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(this, "Thanh toán VNPAY thành công!", Toast.LENGTH_SHORT).show();
                        handleSubmitOrder("VNPAY");
                    } else {
                        Toast.makeText(this, "Thanh toán thất bại hoặc bị hủy", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        setupClickListeners();

        viewModel.getOrderSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        tvCheckoutTitle.setOnClickListener(v -> openMapActivity());
        btnSelectLocation.setOnClickListener(v -> openMapActivity());
        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());
    }

    private void openMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        String currentAddress = etShippingAddress.getText().toString().trim();
        if (!currentAddress.isEmpty()) {
            intent.putExtra("current_address", currentAddress);
        }
        mapActivityLauncher.launch(intent);
    }

    private void updateTotalAmount() {
        double total = calculateTotalAmount();
        tvTotalAmount.setText("Tổng tiền: " + decimalFormat.format(total) + " VND");
    }

    private double calculateTotalAmount() {
        double total = 0;
        for (CartDisplayItem item : selectedItems) {
            total += item.getProduct().getPrice() * item.getCartItem().getQuantity();
        }
        return total;
    }

    private void handlePlaceOrder() {
        String address = etShippingAddress.getText().toString().trim();
        if (address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = calculateTotalAmount();
        int selectedRadioId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedRadioId == R.id.rb_cash_on_delivery) {
            handleSubmitOrder("COD");
        } else if (selectedRadioId == R.id.rb_bank_transfer) {
            Intent intent = new Intent(this, VnPayWebViewActivity.class);
            intent.putExtra("amount", total);
            vnPayLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleSubmitOrder(String paymentMethod) {
        String address = etShippingAddress.getText().toString().trim();
        double total = calculateTotalAmount();
        viewModel.placeOrder(paymentMethod, address, total, selectedItems);
    }
}
