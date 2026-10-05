package com.example.houserentalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.houserentalapp.R;
import com.example.houserentalapp.model.RentalApplication;
import com.example.houserentalapp.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ApplicationCardAdapter extends RecyclerView.Adapter<ApplicationCardAdapter.ViewHolder> {

    public interface WithdrawListener {
        void onWithdraw(RentalApplication application, int position);
    }

    private final Context context;
    private List<RentalApplication> applications;
    private final WithdrawListener withdrawListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public ApplicationCardAdapter(Context context, List<RentalApplication> applications,
                                   WithdrawListener withdrawListener) {
        this.context = context;
        this.applications = applications;
        this.withdrawListener = withdrawListener;
    }

    public void updateData(List<RentalApplication> newList) {
        this.applications = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RentalApplication app = applications.get(position);

        holder.tvTitle.setText(app.getPropertyTitle() != null ? app.getPropertyTitle() : "Property");
        holder.tvAppliedDate.setText("Applied " + dateFormat.format(new Date(app.getAppliedAt())));
        holder.tvMessagePreview.setText(
                (app.getMessage() != null && !app.getMessage().isEmpty())
                        ? app.getMessage()
                        : "No message added");

        // Load property image
        if (app.getPropertyImage() != null && !app.getPropertyImage().isEmpty()) {
            Glide.with(context)
                    .load(app.getPropertyImage())
                    .placeholder(R.drawable.placeholder_property)
                    .centerCrop()
                    .into(holder.ivPropertyThumb);
        } else {
            holder.ivPropertyThumb.setImageResource(R.drawable.placeholder_property);
        }

        // Status badge
        applyStatusBadge(holder.tvStatusBadge, app.getStatus());

        // Withdraw button — only show when status allows it
        boolean canWithdraw = Constants.APP_STATUS_PENDING.equals(app.getStatus())
                || Constants.APP_STATUS_REVIEWING.equals(app.getStatus());
        holder.btnWithdraw.setVisibility(canWithdraw ? View.VISIBLE : View.GONE);

        holder.btnWithdraw.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_ID) {
                withdrawListener.onWithdraw(applications.get(adapterPosition), adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return applications.size();
    }

    private void applyStatusBadge(TextView badge, String status) {
        if (status == null) status = Constants.APP_STATUS_PENDING;
        switch (status) {
            case Constants.APP_STATUS_ACCEPTED:
                badge.setText("Accepted");
                badge.setBackgroundResource(R.drawable.bg_badge_confirmed);
                badge.setTextColor(ContextCompat.getColor(context, R.color.status_confirmed));
                break;
            case Constants.APP_STATUS_REJECTED:
                badge.setText("Rejected");
                badge.setBackgroundResource(R.drawable.bg_badge_rejected);
                badge.setTextColor(ContextCompat.getColor(context, R.color.status_rejected));
                break;
            case Constants.APP_STATUS_REVIEWING:
                badge.setText("Under Review");
                badge.setBackgroundResource(R.drawable.bg_badge_pending);
                badge.setTextColor(ContextCompat.getColor(context, R.color.info_blue));
                break;
            case Constants.APP_STATUS_WITHDRAWN:
                badge.setText("Withdrawn");
                badge.setBackgroundResource(R.drawable.bg_badge_pending);
                badge.setTextColor(ContextCompat.getColor(context, R.color.on_surface_secondary));
                break;
            default: // pending
                badge.setText("Pending");
                badge.setBackgroundResource(R.drawable.bg_badge_pending);
                badge.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPropertyThumb;
        TextView tvTitle, tvAppliedDate, tvStatusBadge, tvMessagePreview;
        MaterialButton btnWithdraw;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropertyThumb = itemView.findViewById(R.id.iv_property_thumb);
            tvTitle = itemView.findViewById(R.id.tv_property_title);
            tvAppliedDate = itemView.findViewById(R.id.tv_applied_date);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvMessagePreview = itemView.findViewById(R.id.tv_message_preview);
            btnWithdraw = itemView.findViewById(R.id.btn_withdraw);
        }
    }
}
