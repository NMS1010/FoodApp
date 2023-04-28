package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ms.food_app.databinding.ReviewItemBinding;
import com.ms.food_app.models.Review;
import com.ms.food_app.utils.LoadingUtil;

import java.util.List;

public class CustomerReviewAdapter extends RecyclerView.Adapter<CustomerReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviews;
    private ProgressDialog progress;

    public CustomerReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        progress = LoadingUtil.setLoading(context);
    }
    @NonNull
    @Override
    public CustomerReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewItemBinding binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomerReviewAdapter.ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        Glide.with(context).load(review.getUserReviewDto().getAvatar()).into(holder.binding.imageUser);
        holder.binding.nameUser.setText(review.getUserReviewDto().getFirstname() + " " + review.getUserReviewDto().getLastname());
        holder.binding.reviewRate.setText(review.getRating() + " out of 5.0");
        holder.binding.reviewComment.setText(review.getContent());
        holder.binding.reviewDate.setText(review.getCreatedAt().toLocaleString());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateReview(List<Review> reviews){
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ReviewItemBinding binding;

        public ReviewViewHolder(@NonNull ReviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}