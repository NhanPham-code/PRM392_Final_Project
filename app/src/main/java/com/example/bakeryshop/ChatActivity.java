package com.example.bakeryshop;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.bakeryshop.ViewModel.ProfileViewModel;
import com.example.bakeryshop.Data.DTO.ReadUserDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Message data class
class UserMessage {
    private String sender;
    private String text;
    private long timestamp;

    // Constructor rỗng cho Firebase
    public UserMessage() {
        this.sender = "";
        this.text = "";
        this.timestamp = 0;
    }

    // Constructor đầy đủ
    public UserMessage(String sender, String text, long timestamp) {
        this.sender = sender;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters và Setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

// UserInfo class để cập nhật danh sách user
class UserContactInfo {
    private String userId;
    private String name;
    private String lastMessage;
    private long timestamp;

    public UserContactInfo() {}

    public UserContactInfo(String userId, String name, String lastMessage, long timestamp) {
        this.userId = userId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    // Getters và Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

public class ChatActivity extends AppCompatActivity {
    private String currentUserId = "";
    private String currentUserName = "";
    private String roomId = "";
    private DatabaseReference dbRef;
    private List<UserMessage> messagesList = new ArrayList<>();
    private ScrollView scrollView;
    private final String ADMIN_ID = "admin";

    // ViewModel để lấy thông tin user
    private ProfileViewModel profileViewModel;
    private ReadUserDTO userProfile;

    // UI components
    private EditText input;
    private Button sendBtn;
    private LinearLayout chatLayout;
    private TextView headerText;
    private Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Khởi tạo ViewModel
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Khởi tạo các view
        initViews();

        // Lấy thông tin user từ ViewModel
        loadUserProfile();

        // Observe profile data
        observeProfileData();
    }

    private void initViews() {
        input = findViewById(R.id.msgInput);
        sendBtn = findViewById(R.id.sendBtn);
        chatLayout = findViewById(R.id.chatLayout);
        headerText = findViewById(R.id.headerText);
        backBtn = findViewById(R.id.backBtn);
        scrollView = findViewById(R.id.scrollView);

        // Set header title
        headerText.setText("Chat với Admin");

        // Xử lý nút quay lại
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        // Fetch user profile từ ViewModel
        profileViewModel.fetchUserProfile();
    }

    private void observeProfileData() {
        // Observe thành công
        profileViewModel.getProfileSuccess.observe(this, user -> {
            if (user != null) {
                userProfile = user;
                currentUserId = String.valueOf(user.getUserId()); // Chuyển từ int sang String
                currentUserName = user.getFullName();

                // Sau khi có thông tin user, khởi tạo chat
                initializeChat();
            }
        });

        // Observe lỗi (nếu ViewModel có)
        // profileViewModel.getProfileError.observe(this, error -> {
        //     Toast.makeText(this, "Không thể tải thông tin user: " + error, Toast.LENGTH_SHORT).show();
        //     // Sử dụng thông tin fallback
        //     useFallbackUserInfo();
        // });
    }

    private void useFallbackUserInfo() {
        // Fallback nếu không lấy được thông tin từ ViewModel
        currentUserId = "user" + System.currentTimeMillis();
        currentUserName = "User " + currentUserId;
        initializeChat();
    }

    private void initializeChat() {
        // Kiểm tra thông tin user
        if (currentUserId == null || currentUserId.isEmpty()) {
            useFallbackUserInfo();
            return;
        }

        // Tạo roomId (sắp xếp để đảm bảo cùng 1 room với admin)
        if (currentUserId.compareTo(ADMIN_ID) < 0) {
            roomId = currentUserId + "_" + ADMIN_ID;
        } else {
            roomId = ADMIN_ID + "_" + currentUserId;
        }

        // Kết nối database
        dbRef = FirebaseDatabase.getInstance().getReference("chatrooms").child(roomId);

        // Xử lý gửi tin nhắn
        sendBtn.setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
                input.setText("");
            }
        });

        // Lắng nghe tin nhắn
        listenForMessages(chatLayout);
    }

    private void sendMessage(String text) {
        String key = dbRef.push().getKey();
        if (key != null) {
            UserMessage msg = new UserMessage(currentUserId, text, new Date().getTime());
            dbRef.child(key).setValue(msg)
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật user_list để admin biết user này đã nhắn tin
                        updateUserInList(text);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Không thể gửi tin nhắn", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUserInList(String lastMessage) {
        // Cập nhật thông tin user trong user_list để admin thấy
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("user_list").child(currentUserId);

        UserContactInfo userInfo = new UserContactInfo(
                currentUserId,
                currentUserName,
                lastMessage,
                new Date().getTime()
        );

        userRef.setValue(userInfo)
                .addOnSuccessListener(aVoid -> {
                    // Thành công
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi
                });
    }

    private void listenForMessages(LinearLayout chatLayout) {
        // Load tin nhắn theo timestamp, cũ nhất lên trên
        dbRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                messagesList.clear();
                chatLayout.removeAllViews();

                // Thu thập tất cả tin nhắn
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    UserMessage msg = messageSnapshot.getValue(UserMessage.class);
                    if (msg != null) {
                        messagesList.add(msg);
                    }
                }

                // Hiển thị tin nhắn
                for (UserMessage msg : messagesList) {
                    displayMessage(msg, chatLayout);
                }

                // Tự động scroll xuống tin nhắn mới nhất
                scrollToBottom();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scrollToBottom() {
        // Scroll xuống cuối sau khi messages được load
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void displayMessage(UserMessage msg, LinearLayout chatLayout) {
        LinearLayout messageContainer = new LinearLayout(this);
        messageContainer.setOrientation(LinearLayout.VERTICAL);
        messageContainer.setPadding(16, 8, 16, 8);

        // Tin nhắn chính
        TextView messageText = new TextView(this);
        messageText.setTextSize(16f);
        messageText.setPadding(16, 12, 16, 12);

        // Thời gian
        TextView timeText = new TextView(this);
        timeText.setTextSize(12f);
        timeText.setTextColor(0xFF666666);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String formattedTime = dateFormat.format(new Date(msg.getTimestamp()));

        if (msg.getSender().equals(currentUserId)) {
            // Tin nhắn của mình - bên phải
            messageText.setText(msg.getText());
            messageText.setBackgroundResource(R.drawable.message_bubble_right);
            messageText.setTextColor(0xFFFFFFFF);

            timeText.setText("Tôi • " + formattedTime);
            timeText.setGravity(Gravity.END);

            messageContainer.setGravity(Gravity.END);
        } else {
            // Tin nhắn của admin - bên trái
            messageText.setText(msg.getText());
            messageText.setBackgroundResource(R.drawable.message_bubble_left);
            messageText.setTextColor(0xFF000000);

            timeText.setText("Admin • " + formattedTime);
            timeText.setGravity(Gravity.START);

            messageContainer.setGravity(Gravity.START);
        }

        messageContainer.addView(messageText);
        messageContainer.addView(timeText);
        chatLayout.addView(messageContainer);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}