package com.example.bakeryshop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.bakeryshop.Data.Repository.FeedbackRepository;
import com.example.bakeryshop.Adapters.FeedbackAdapter;
import com.example.bakeryshop.ViewModel.FeedbackViewModel;
import com.example.bakeryshop.ViewModel.ProfileViewModel;
import com.example.bakeryshop.databinding.ActivityFeedbackListBinding;

public class FeedbackListActivity extends AppCompatActivity {

    private ActivityFeedbackListBinding binding;
    private FeedbackViewModel feedbackListViewModel;
    private ProfileViewModel profileViewModel;
    private FeedbackAdapter feedbackAdapter;
    private static final String TAG = "FeedbackListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityFeedbackListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        feedbackListViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);


        feedbackAdapter = new FeedbackAdapter();
        binding.rvFeedbackList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFeedbackList.setAdapter(feedbackAdapter);
        feedbackListViewModel.fetchFeedbacks();
        observeViewModel();

        // Nút "Phản hồi của bạn" dẫn tới trang MyFeedbackActivity
        binding.btnMyFeedback.setText("Phản hồi của bạn");
        binding.btnMyFeedback.setOnClickListener(v -> {

            Intent intent = new Intent(FeedbackListActivity.this, MyFeedbackActivity.class);
            startActivity(intent);
        });
        // Quan sát thay đổi feedback để tự động reload
        FeedbackRepository.getInstance(getApplicationContext())
                .feedbackChanged.observe(this, changed -> {
                    if (Boolean.TRUE.equals(changed)) {
                        Log.d(TAG, "Feedback thay đổi -> reload danh sách");
                        feedbackListViewModel.fetchFeedbacks();
                    }
                });
    }

    private void observeViewModel() {
        // Quan sát danh sách feedback để hiển thị lên RecyclerView
        feedbackListViewModel.feedbacks.observe(this, feedbacks -> {
            if (feedbacks != null && !feedbacks.isEmpty()) {
                feedbackAdapter.setFeedbackList(feedbacks);
                binding.rvFeedbackList.setVisibility(android.view.View.VISIBLE);
            } else {
                feedbackAdapter.setFeedbackList(null);
                Toast.makeText(this, "Không có phản hồi nào.", Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát trạng thái loading
        feedbackListViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        // Quan sát lỗi
        feedbackListViewModel.errorMessage.observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

    }
}
