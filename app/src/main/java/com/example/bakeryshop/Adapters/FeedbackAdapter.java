package com.example.bakeryshop.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakeryshop.Data.DTO.FeedbackResponseDTO;
import com.example.bakeryshop.databinding.ItemFeedbackBinding;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<FeedbackResponseDTO> feedbackList;

    public FeedbackAdapter() {
    }
    public interface OnFeedbackActionListener {
        void onUpdate(FeedbackResponseDTO feedback);
        void onDelete(FeedbackResponseDTO feedback);
    }
    public void setFeedbackList(List<FeedbackResponseDTO> feedbackList) {
        this.feedbackList = feedbackList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeedbackBinding binding = ItemFeedbackBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FeedbackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        FeedbackResponseDTO feedback = feedbackList.get(position);
        holder.bind(feedback);
    }

    @Override
    public int getItemCount() {
        return feedbackList != null ? feedbackList.size() : 0;
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {

        private final ItemFeedbackBinding binding;

        public FeedbackViewHolder(ItemFeedbackBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FeedbackResponseDTO feedback) {
            binding.tvFeedbackContent.setText(feedback.getDescription());

            String rawDate = feedback.getSubmittedDate();
            if (rawDate != null && rawDate.contains("T")) {
                String dateOnly = rawDate.split("T")[0];
                binding.tvFeedbackDate.setText(dateOnly);
            } else {
                binding.tvFeedbackDate.setText(rawDate != null ? rawDate : "N/A");
            }

            // TODO: Nếu muốn click vào feedback để xem chi tiết
            // itemView.setOnClickListener(v -> {
            //     Toast.makeText(itemView.getContext(), "Clicked feedback ID: " + feedback.getFeedbackId(), Toast.LENGTH_SHORT).show();
            // });
        }
    }
}
