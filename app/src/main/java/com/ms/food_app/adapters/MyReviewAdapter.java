package com.ms.food_app.adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.ms.food_app.activities.Feedback;
import com.ms.food_app.databinding.MyReviewItemBinding;
import com.ms.food_app.databinding.ReviewItemBinding;
import com.ms.food_app.models.Product;
import com.ms.food_app.models.Review;
import com.ms.food_app.services.BaseAPIService;
import com.ms.food_app.services.IProductService;
import com.ms.food_app.utils.LoadingUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.ReviewViewHolder> {
    private Context context;
    private List<Review> reviews;
    private ProgressDialog progress;

    public MyReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
        progress = LoadingUtil.setLoading(context);
    }
    @NonNull
    @Override
    public MyReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyReviewItemBinding binding = MyReviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyReviewAdapter.ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyReviewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        progress.show();
        BaseAPIService.createService(IProductService.class).getProductById(review.getProductId()).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful() && response.body() != null){
                    Product product = response.body();
                    Glide.with(context).load(product.getImages().get(0)).into(holder.binding.imageFood);
                    holder.binding.nameFood.setText(product.getName());
                    holder.binding.date.setText(review.getCreatedAt().toLocaleString());
                    holder.binding.review.setText(review.getContent());
                    holder.binding.ratingBar.setRating(review.getRating());
                    holder.itemView.setOnClickListener(view -> {

                        Intent intent = new Intent(context, Feedback.class);
                        intent.putExtra("orderId", review.getOrderId());
                        intent.putExtra("product", new Gson().toJson(product));
                        intent.putExtra("review", new Gson().toJson(review));
                        context.startActivity(intent);
                    });
                }
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.d("Error", t.getMessage());
                progress.dismiss();
            }
        });

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
        private MyReviewItemBinding binding;

        public ReviewViewHolder(@NonNull MyReviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
