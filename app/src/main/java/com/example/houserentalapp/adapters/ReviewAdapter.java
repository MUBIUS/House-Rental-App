package com.example.houserentalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.Review;
import com.example.houserentalapp.utils.DateUtils;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final Context context;
    private final List<Review> reviews;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.tvReviewerName.setText(review.getTenantName() != null ? review.getTenantName() : "Anonymous");
        holder.tvDate.setText(DateUtils.formatDate(review.getCreatedAt()));
        holder.tvRating.setText(String.valueOf(review.getRating()));
        holder.tvComment.setText(review.getComment());

        if (review.getLandlordResponse() != null && !review.getLandlordResponse().isEmpty()) {
            holder.layoutResponse.setVisibility(View.VISIBLE);
            holder.tvResponse.setText(review.getLandlordResponse());
        } else {
            holder.layoutResponse.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateData(List<Review> newReviews) {
        this.reviews.clear();
        this.reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerName, tvDate, tvRating, tvComment, tvResponse;
        LinearLayout layoutResponse;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerName = itemView.findViewById(R.id.tv_reviewer_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvComment = itemView.findViewById(R.id.tv_comment);
            tvResponse = itemView.findViewById(R.id.tv_response);
            layoutResponse = itemView.findViewById(R.id.layout_response);
        }
    }
}
