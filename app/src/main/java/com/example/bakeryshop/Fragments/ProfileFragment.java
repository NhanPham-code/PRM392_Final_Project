package com.example.bakeryshop.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bakeryshop.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
}