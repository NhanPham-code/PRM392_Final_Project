package com.example.bakeryshop;

import static android.content.Intent.getIntent;
import com.example.bakeryshop.Data.Repository.FeedbackRepository;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bakeryshop.Data.DTO.FeedbackResponseDTO;
import com.example.bakeryshop.ViewModel.FeedbackViewModel;

public class MyFeedbackActivity extends AppCompatActivity {

    private EditText etFeedbackContent;
    private Button btnSave, btnDelete;
    private FeedbackViewModel feedbackViewModel;
    private FeedbackResponseDTO myFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_feedback);

        etFeedbackContent = findViewById(R.id.etFeedbackContent);
        btnSave = findViewById(R.id.btnSaveFeedback);
        btnDelete = findViewById(R.id.btnDeleteFeedback);

        feedbackViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);

        feedbackViewModel.fetchMyFeedback();

        feedbackViewModel.myFeedback.observe(this, feedback -> {
            myFeedback = feedback;
            if (feedback != null) {
                etFeedbackContent.setText(feedback.getDescription());
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                etFeedbackContent.setText("");
                btnDelete.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(v -> {
            String content = etFeedbackContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            if (myFeedback == null) {
                // Thêm mới (gọi addFeedback có userId, backend tự lấy từ token)
                feedbackViewModel.addFeedback(content,
                        () -> {
                            Toast.makeText(this, "Thêm phản hồi thành công", Toast.LENGTH_SHORT).show();
                            FeedbackRepository.getInstance(getApplicationContext()).notifyChange(); // Gọi notify
                            finish(); // Quay lại ListFeedbackActivity
                        },
                        () -> Toast.makeText(this, "Thêm phản hồi thất bại", Toast.LENGTH_SHORT).show()
                );
            } else {
                // Cập nhật
                Log.d("UpdateFeedback", "Gọi update với feedbackId: " + myFeedback.getFeedbackId());

                feedbackViewModel.updateFeedback(myFeedback.getFeedbackId(), content,
                        () -> {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            FeedbackRepository.getInstance(getApplicationContext()).notifyChange(); // Gọi notify
                            finish();
                        },
                        () -> Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                );
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (myFeedback != null) {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận xoá")
                        .setMessage("Bạn có chắc muốn xoá phản hồi này?")
                        .setPositiveButton("Xoá", (dialog, which) -> {
                            feedbackViewModel.deleteFeedback(myFeedback.getUserId(),
                                    () -> {
                                        Toast.makeText(this, "Xoá thành công", Toast.LENGTH_SHORT).show();
                                        FeedbackRepository.getInstance(getApplicationContext()).notifyChange(); // Gọi notify
                                        finish();
                                    },
                                    () -> Toast.makeText(this, "Xoá thất bại", Toast.LENGTH_SHORT).show()
                            );
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            }
        });
    }
}