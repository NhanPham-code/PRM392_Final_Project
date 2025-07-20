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
    private TextInputEditText etShippingAddress;
    private RadioGroup rgPaymentMethod;
    private MaterialButton btnPlaceOrder;

    private CartDisplayItemAdapter adapter;
    private List<CartDisplayItem> selectedItems = new ArrayList<>();
    private CheckoutViewModel viewModel;
    private ActivityResultLauncher<Intent> vnPayLauncher;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        recyclerView = findViewById(R.id.recycler_checkout_items);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        etShippingAddress = findViewById(R.id.et_shipping_address);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        viewModel = new CheckoutViewModel(getApplicationContext());

        selectedItems = (List<CartDisplayItem>) getIntent().getSerializableExtra("selected_items");
        if (selectedItems == null) selectedItems = new ArrayList<>();

        adapter = new CartDisplayItemAdapter(selectedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateTotalAmount();

        // Đăng ký VNPay launcher trước khi dùng
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

        btnPlaceOrder.setOnClickListener(v -> handlePlaceOrder());

        viewModel.getOrderSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                // Quay về MainActivity thay vì chỉ finish()
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
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
        }
    }

    private void handleSubmitOrder(String paymentMethod) {
        String address = etShippingAddress.getText().toString().trim();
        double total = calculateTotalAmount();
        viewModel.placeOrder(paymentMethod, address, total, selectedItems);
    }
}

