package com.example.bakeryshop.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.LoginActivity;
import com.example.bakeryshop.R;
import com.example.bakeryshop.ViewModel.ProfileViewModel;
import com.example.bakeryshop.databinding.FragmentProfileBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "BakeryShopPrefs";
    private static final String KEY_TOKEN = "auth_token";

    private ProfileViewModel profileViewModel;

    private ReadUserDTO userProfile;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Required empty public constructor
        profileViewModel = new ProfileViewModel(getActivity().getApplication());

        // Fetch user profile data when the fragment is created
        profileViewModel.fetchUserProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        // Find the LinearLayout within the inflated view
        LinearLayout layoutPersonalInfo = binding.layoutPersonalInfo;
        layoutPersonalInfo.setOnClickListener(v -> openPersonalInfoDialog());

        LinearLayout layoutAccountSetting = binding.layoutAccountSettings;
        layoutAccountSetting.setOnClickListener(v -> openAccountSettingsDialog());

        // Observe the ViewModel for user profile data
        profileViewModel.getProfileSuccess.observe(getViewLifecycleOwner(), user -> {
            // Update UI with user data
            userProfile = user; // Save the user profile data
            binding.tvUserName.setText(user.getFullName());
        });

        // logout
        binding.btnLogout.setOnClickListener(v -> {
            // Handle logout logic here
            Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
            // You can also navigate to the login screen or clear user session data here
            removeToken(); // Call method to remove token

            // Move to LoginActivity
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        return binding.getRoot(); // Return the inflated view
    }

    private void openPersonalInfoDialog() {
        // Lấy view từ layout tùy chỉnh
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_personal_info, null);

        // Gán dữ liệu từ userProfile vào các trường trong dialog
        if (userProfile != null) {
            TextInputEditText etFullName = dialogView.findViewById(R.id.etFullName);
            TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
            TextInputEditText etPhoneNumber = dialogView.findViewById(R.id.etPhone);
            TextInputEditText etAddress = dialogView.findViewById(R.id.etAddress);
            TextInputEditText etRegistrationDate = dialogView.findViewById(R.id.etRegistrationDate);

            // Set existing user data to the dialog fields
            etFullName.setText(userProfile.getFullName());
            etEmail.setText(userProfile.getEmail());
            etPhoneNumber.setText(userProfile.getPhoneNumber());
            etAddress.setText(userProfile.getAddress());

            // Format and set registration date
            if (userProfile.getRegistrationDate() != null) {
                String formattedDate = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // For Android O (API 26) and higher, use java.time.LocalDateTime
                    // Assuming userProfile.getRegistrationDate() returns a String in "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS" format
                    String registrationDateString = userProfile.getRegistrationDate(); // Get the string date

                    try {
                        // Parse the string directly to LocalDateTime
                        LocalDateTime dateTime = LocalDateTime.parse(registrationDateString);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        formattedDate = formatter.format(dateTime); // Format the LocalDateTime
                    } catch (DateTimeParseException e) {
                        // Handle parsing error, e.g., log it or set a default
                        formattedDate = "Invalid Date";
                        e.printStackTrace();
                    }

                } else {
                    formattedDate = "N/A";
                }
                etRegistrationDate.setText(formattedDate);
            } else {
                etRegistrationDate.setText(""); // Set empty if no registration date
            }
        }

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