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
import com.example.houserentalapp.model.Lease;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LeaseCardAdapter extends RecyclerView.Adapter<LeaseCardAdapter.ViewHolder> {

    private final Context context;
    private List<Lease> leases;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public LeaseCardAdapter(Context context, List<Lease> leases) {
        this.context = context;
        this.leases = leases;
    }

    public void updateData(List<Lease> newList) {
        this.leases = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lease_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lease lease = leases.get(position);

        holder.tvPropertyTitle.setText(lease.getPropertyTitle() != null ? lease.getPropertyTitle() : "Property");
        holder.tvAddress.setText(lease.getPropertyAddress() != null ? lease.getPropertyAddress() : "");

        // Rent
        String rent = CurrencyUtils.formatPerMonth((long) lease.getMonthlyRent(), lease.getCurrency());
        holder.tvRent.setText(rent);

        // Lease period
        String period = "";
        if (lease.getStartDate() > 0 && lease.getEndDate() > 0) {
            period = dateFormat.format(new Date(lease.getStartDate()))
                    + " – "
                    + dateFormat.format(new Date(lease.getEndDate()));
        }
        holder.tvLeasePeriod.setText(period.isEmpty() ? "No dates set" : period);

        // Property image
        if (lease.getPropertyImage() != null && !lease.getPropertyImage().isEmpty()) {
            Glide.with(context)
                    .load(lease.getPropertyImage())
                    .placeholder(R.drawable.placeholder_property)
                    .centerCrop()
                    .into(holder.ivPropertyImage);
        } else {
            holder.ivPropertyImage.setImageResource(R.drawable.placeholder_property);
        }

        // Status badge
        applyStatusBadge(holder.tvStatusBadge, lease.getStatus());
    }

    @Override
    public int getItemCount() {
        return leases.size();
    }

    private void applyStatusBadge(TextView badge, String status) {
        if (status == null) status = Constants.LEASE_STATUS_ACTIVE;
        switch (status) {
            case Constants.LEASE_STATUS_ACTIVE:
                badge.setText("Active");
                badge.setBackgroundResource(R.drawable.bg_badge_confirmed);
                badge.setTextColor(ContextCompat.getColor(context, R.color.status_confirmed));
                break;
            case Constants.LEASE_STATUS_TERMINATED:
                badge.setText("Terminated");
                badge.setBackgroundResource(R.drawable.bg_badge_rejected);
                badge.setTextColor(ContextCompat.getColor(context, R.color.status_rejected));
                break;
            default: // expired or other
                badge.setText("Expired");
                badge.setBackgroundResource(R.drawable.bg_badge_pending);
                badge.setTextColor(ContextCompat.getColor(context, R.color.status_pending));
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPropertyImage;
        TextView tvPropertyTitle, tvAddress, tvRent, tvLeasePeriod, tvStatusBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropertyImage = itemView.findViewById(R.id.iv_property_image);
            tvPropertyTitle = itemView.findViewById(R.id.tv_property_title);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvRent = itemView.findViewById(R.id.tv_rent);
            tvLeasePeriod = itemView.findViewById(R.id.tv_lease_period);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
        }
    }
}
