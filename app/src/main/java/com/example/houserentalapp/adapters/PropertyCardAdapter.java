package com.example.houserentalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.ImageUtils;
import com.example.houserentalapp.utils.SessionManager;

import java.util.List;

public class PropertyCardAdapter extends RecyclerView.Adapter<PropertyCardAdapter.ViewHolder> {

    private final Context context;
    private final List<Property> properties;
    private final OnPropertyClickListener listener;
    private final String userCurrency;

    public interface OnPropertyClickListener {
        void onPropertyClick(Property property);
        void onFavoriteClick(Property property, ImageView favoriteIcon);
    }

    public PropertyCardAdapter(Context context, List<Property> properties, OnPropertyClickListener listener) {
        this.context = context;
        this.properties = properties;
        this.listener = listener;
        SessionManager session = new SessionManager(context);
        this.userCurrency = session.getCurrency(); // Default to user's currency
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = properties.get(position);

        holder.tvTitle.setText(property.getTitle());
        holder.tvLocation.setText(property.getFullLocation());
        
        // Use the property's set currency to format the price. 
        // If we want currency conversion we would do it here, but for now just display in property's currency.
        String currency = property.getCurrency();
        holder.tvPrice.setText(CurrencyUtils.formatPerMonth(property.getPrice(), currency));
        
        holder.tvPropertyType.setText(property.getPropertyType());
        
        if (property.isFeatured()) {
            holder.tvFeatured.setVisibility(View.VISIBLE);
        } else {
            holder.tvFeatured.setVisibility(View.GONE);
        }

        holder.tvBeds.setText(property.getBedrooms() + " Beds");
        holder.tvBaths.setText(property.getBathrooms() + " Baths");
        holder.tvArea.setText((int)property.getArea() + " sqft");

        ImageUtils.loadThumbnail(context, property.getCoverImage(), holder.ivPropertyImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPropertyClick(property);
        });

        holder.ivFavorite.setOnClickListener(v -> {
            if (listener != null) listener.onFavoriteClick(property, holder.ivFavorite);
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void updateData(List<Property> newProperties) {
        this.properties.clear();
        this.properties.addAll(newProperties);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPropertyImage, ivFavorite;
        TextView tvTitle, tvLocation, tvPrice, tvPropertyType, tvFeatured;
        TextView tvBeds, tvBaths, tvArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropertyImage = itemView.findViewById(R.id.iv_property_image);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPropertyType = itemView.findViewById(R.id.tv_property_type);
            tvFeatured = itemView.findViewById(R.id.tv_featured);
            tvBeds = itemView.findViewById(R.id.tv_beds);
            tvBaths = itemView.findViewById(R.id.tv_baths);
            tvArea = itemView.findViewById(R.id.tv_area);
        }
    }
}
