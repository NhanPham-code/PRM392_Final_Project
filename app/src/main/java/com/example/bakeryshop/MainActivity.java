package com.example.bakeryshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bakeryshop.Fragments.CartFragment;
import com.example.bakeryshop.Fragments.ListProductFragment;
import com.example.bakeryshop.Fragments.NotificationFragment;
import com.example.bakeryshop.Fragments.ProfileFragment;
import com.example.bakeryshop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ListProductFragment listProductFragment;
    private CartFragment cartFragment;
    private ProfileFragment profileFragment;
    private NotificationFragment notificationFragment;

    // SharedPreferences để lưu trữ token
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        listProductFragment = new ListProductFragment();
        replaceFragment(listProductFragment);

        // Drawer setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation Drawer View setup
        binding.navigationView.setNavigationItemSelectedListener(item -> {
            // check login before opening
            if (isLogin()) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return false; // Nếu chưa đăng nhập, không mở fragment
            }
            return true;
        });

        // Bottom Navigation View setup
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_products) {
                selectedFragment = listProductFragment;
                Toast.makeText(this, "Products clicked", Toast.LENGTH_SHORT).show();
            }
            else if (item.getItemId() == R.id.nav_notifications) {
                // Kiểm tra đăng nhập trước khi mở NotificationFragment
                if (!isLogin()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return false; // Nếu chưa đăng nhập, không mở fragment
                }
                // Nếu đã đăng nhập, mở NotificationFragment
                if (notificationFragment == null) {
                    notificationFragment = new NotificationFragment();
                }
                selectedFragment = notificationFragment;
                Toast.makeText(this, "Notification clicked", Toast.LENGTH_SHORT).show();
            }
            else if (item.getItemId() == R.id.nav_carts) {
                // Kiểm tra đăng nhập trước khi mở CartFragment
                if (!isLogin()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return false; // Nếu chưa đăng nhập, không mở fragment
                }
                // Nếu đã đăng nhập, mở CartFragment
                if (cartFragment == null) {
                    cartFragment = new CartFragment();
                }
                selectedFragment = cartFragment;
                Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show();
            }
            else if (item.getItemId() == R.id.nav_profile) {
                // Kiểm tra đăng nhập trước khi mở ProfileFragment
                if (!isLogin()) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return false; // Nếu chưa đăng nhập, không mở fragment
                }
                // Nếu đã đăng nhập, mở ProfileFragment
                if (profileFragment == null) {
                    profileFragment = new ProfileFragment();
                }
                selectedFragment = profileFragment;
                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
            }
            replaceFragment(selectedFragment);
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private boolean isLogin() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = sharedPreferences.getString(KEY_TOKEN, null);
        if (token != null) {
            return true; // Người dùng đã đăng nhập
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show();
            return false; // Người dùng chưa đăng nhập
        }
    }
}