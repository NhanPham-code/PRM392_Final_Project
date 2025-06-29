package com.example.bakeryshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bakeryshop.ViewModel.LoginViewModel;
import com.example.bakeryshop.databinding.ActivityLoginBinding;

import java.io.Console;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Quan sát LiveData từ ViewModel
        // Quan sát kết quả đăng nhập
        loginViewModel.loginSuccess.observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                 startActivity(intent);
                 finish();
            }
        });

        // Quan sát trạng thái loading
        loginViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE); // Hiển thị ProgressBar
                binding.buttonLogin.setEnabled(false); // Vô hiệu hóa nút đăng nhập khi đang tải
                binding.editTextEmail.setEnabled(false);
                binding.editTextPassword.setEnabled(false);
            } else {
                binding.progressBar.setVisibility(View.GONE); // Ẩn ProgressBar
                binding.buttonLogin.setEnabled(true); // Bật lại nút đăng nhập
                binding.editTextEmail.setEnabled(true);
                binding.editTextPassword.setEnabled(true);
            }
        });

        // Quan sát thông báo lỗi
        loginViewModel.errorMessage.observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                // Xóa lỗi sau khi hiển thị để tránh hiển thị lại khi xoay màn hình
                loginViewModel.clearErrorMessage(); // Đặt lại lỗi để không hiển thị lại khi cấu hình thay đổi
            }
        });

        binding.buttonLogin.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            // Kiểm tra xem email và mật khẩu có rỗng không
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi phương thức đăng nhập từ ViewModel
            loginViewModel.login(email, password);
        });

        binding.textViewSignup.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.backButton.setOnClickListener(v -> {
            finish();
        });
    }
}