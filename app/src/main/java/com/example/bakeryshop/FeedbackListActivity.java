package com.example.bakeryshop;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.bakeryshop.Adapters.FeedbackAdapter;
import com.example.bakeryshop.Data.DTO.FeedbackResponseDTO;
import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.example.bakeryshop.ViewModel.FeedbackViewModel;
import com.example.bakeryshop.ViewModel.ProfileViewModel;
import com.example.bakeryshop.databinding.ActivityFeedbackListBinding;

import java.util.List;

public class FeedbackListActivity extends AppCompatActivity {

    private ActivityFeedbackListBinding binding;
    private FeedbackViewModel feedbackListViewModel;
    private ProfileViewModel profileViewModel;
    private FeedbackAdapter feedbackAdapter;
    private FeedbackResponseDTO myFeedback = null;
    private static final String TAG = "FeedbackListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityFeedbackListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        feedbackListViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        feedbackAdapter = new FeedbackAdapter();
        binding.rvFeedbackList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFeedbackList.setAdapter(feedbackAdapter);

        observeViewModel();

        // Load feedback ngay từ đầu
        feedbackListViewModel.fetchFeedbacks();

        binding.btnAddFeedback.setOnClickListener(v -> {
            // Lấy profile trước khi mở dialog
            profileViewModel.fetchUserProfile();
        });

        // Observe profile để mở dialog khi lấy được user
        profileViewModel.getProfileSuccess.observe(this, user -> {
            if (user != null) {
                showAddFeedbackDialogWithUser(user);
            } else {
                Toast.makeText(this, "Không lấy được thông tin user.", Toast.LENGTH_SHORT).show();
            }
        });

        // Lỗi khi lấy profile
        profileViewModel.errorMessage.observe(this, err -> {
            if (err != null && !err.isEmpty()) {
                Toast.makeText(this, "Lỗi khi lấy thông tin user: " + err, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void observeViewModel() {
        feedbackListViewModel.feedbacks.observe(this, feedbacks -> {
            if (feedbacks != null && !feedbacks.isEmpty()) {
                feedbackAdapter.setFeedbackList(feedbacks);
                binding.rvFeedbackList.setVisibility(View.VISIBLE);
            } else {
                feedbackAdapter.setFeedbackList(null);
                Toast.makeText(this, "Không có phản hồi nào.", Toast.LENGTH_SHORT).show();
            }
            // Gọi check feedback cá nhân khi danh sách thay đổi
            checkMyFeedback();
        });

        feedbackListViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                Toast.makeText(this, "Đang tải phản hồi...", Toast.LENGTH_SHORT).show();
            }
        });

        feedbackListViewModel.errorMessage.observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

        feedbackListViewModel.createSuccess.observe(this, success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(this, "Đã thêm phản hồi thành công!", Toast.LENGTH_SHORT).show();
                    feedbackListViewModel.fetchFeedbacks();
                } else {
                    Toast.makeText(this, "Thêm phản hồi thất bại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAddFeedbackDialogWithUser(ReadUserDTO user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm phản hồi");

        final EditText input = new EditText(this);
        input.setHint("Nhập nội dung phản hồi...");
        input.setMinLines(3);
        input.setPadding(40, 40, 40, 40);

        builder.setView(input);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            String description = input.getText().toString().trim();
            if (description.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung phản hồi.", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = user.getUserId();
            Log.d("FeedbackDebug", "Lấy được userId: " + userId);

            boolean hasFeedback = false;
            List<FeedbackResponseDTO> feedbacks = feedbackListViewModel.feedbacks.getValue();
            if (feedbacks != null) {
                for (FeedbackResponseDTO fb : feedbacks) {
                    if (fb.getUserId() == userId) {
                        hasFeedback = true;
                        break;
                    }
                }
            }

            if (hasFeedback) {
                Toast.makeText(this, "Bạn đã gửi phản hồi rồi.", Toast.LENGTH_SHORT).show();
            } else {
                feedbackListViewModel.addFeedback(userId, description,
                        () -> Log.d(TAG, "Thêm phản hồi thành công."),
                        () -> Log.e(TAG, "Thêm phản hồi thất bại."));
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void showUpdateFeedbackDialog(FeedbackResponseDTO feedback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa phản hồi");

        final EditText input = new EditText(this);
        input.setText(feedback.getDescription());
        input.setMinLines(3);
        input.setPadding(40, 40, 40, 40);

        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newDescription = input.getText().toString().trim();
            if (newDescription.isEmpty()) {
                Toast.makeText(this, "Nội dung phản hồi không được để trống.", Toast.LENGTH_SHORT).show();
                return;
            }

            feedbackListViewModel.updateFeedback(feedback.getFeedbackId(), newDescription,
                    () -> {
                        Toast.makeText(this, "Cập nhật phản hồi thành công!", Toast.LENGTH_SHORT).show();
                        feedbackListViewModel.fetchFeedbacks();
                        myFeedback.setDescription(newDescription);
                        updateUIForMyFeedback();
                    },
                    () -> Toast.makeText(this, "Cập nhật phản hồi thất bại.", Toast.LENGTH_SHORT).show()
            );
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void showDeleteFeedbackDialog(FeedbackResponseDTO feedback) {
        new AlertDialog.Builder(this)
                .setTitle("Xoá phản hồi")
                .setMessage("Bạn có chắc chắn muốn xoá phản hồi này không?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    feedbackListViewModel.deleteFeedback(feedback.getFeedbackId(),
                            () -> {
                                Toast.makeText(this, "Xoá phản hồi thành công!", Toast.LENGTH_SHORT).show();
                                myFeedback = null;
                                feedbackListViewModel.fetchFeedbacks();
                                updateUIForMyFeedback();
                            },
                            () -> Toast.makeText(this, "Xoá phản hồi thất bại.", Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkMyFeedback() {
        ReadUserDTO user = profileViewModel.getProfileSuccess.getValue();
        List<FeedbackResponseDTO> feedbacks = feedbackListViewModel.feedbacks.getValue();

        if (user != null && feedbacks != null) {
            for (FeedbackResponseDTO fb : feedbacks) {
                if (fb.getUserId() == user.getUserId()) {
                    myFeedback = fb;
                    break;
                }
            }
        }

        updateUIForMyFeedback();
    }
    private void updateUIForMyFeedback() {
        if (myFeedback != null) {
            binding.layoutMyFeedback.setVisibility(View.VISIBLE);
            binding.tvMyFeedbackContent.setText(myFeedback.getDescription());

            binding.btnEditMyFeedback.setOnClickListener(v -> showUpdateFeedbackDialog(myFeedback));
            binding.btnDeleteMyFeedback.setOnClickListener(v -> showDeleteFeedbackDialog(myFeedback));

            binding.btnAddFeedback.setVisibility(View.GONE); // Ẩn nút thêm
        } else {
            binding.layoutMyFeedback.setVisibility(View.GONE);
            binding.btnAddFeedback.setVisibility(View.VISIBLE); // Hiện nút thêm
        }
    }
}
