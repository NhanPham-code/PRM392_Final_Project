package com.example.bakeryshop.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bakeryshop.LoginActivity;
import com.example.bakeryshop.R;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the LinearLayout within the inflated view
        LinearLayout layoutPersonalInfo = rootView.findViewById(R.id.layoutPersonalInfo);
        layoutPersonalInfo.setOnClickListener(v -> openPersonalInfoDialog());

        LinearLayout layoutAccountSetting = rootView.findViewById(R.id.layoutAccountSettings);
        layoutAccountSetting.setOnClickListener(v -> openAccountSettingsDialog());

        // logout
        MaterialButton btnLogout = rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Handle logout logic here
            Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
            // You can also navigate to the login screen or clear user session data here
            removeToken(); // Call method to remove token

            // Move to LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        return rootView; // Return the inflated view
    }

    private void openPersonalInfoDialog() {
        // Lấy view từ layout tùy chỉnh
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_personal_info, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Personal Information") // Tiêu đề dialog
                .setView(dialogView)              // Gán view tùy chỉnh
                .setPositiveButton("Save", (d, w) -> {
                    savePersonalInfo(dialogView); // Hàm xử lý lưu thông tin
                    Toast.makeText(getContext(), "Personal information updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null) // Nút Hủy, không làm gì khi bấm
                .create();

        dialog.show(); // Hiển thị dialog
    }

    private void openAccountSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_account_settings, null);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Account Settings")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {
                    // Save account settings
                    saveAccountSettings(dialogView);
                    Toast.makeText(getContext(), "Account settings updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void savePersonalInfo(View dialogView) {
    }


    private void saveAccountSettings(View dialogView) {
    }

    private void removeToken() {
        // Logic to remove token or clear user session data
        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN); // Xóa token khỏi SharedPreferences
        editor.apply(); // Lưu thay đổi
        Toast.makeText(getContext(), "Token removed successfully!", Toast.LENGTH_SHORT).show();
    }
}