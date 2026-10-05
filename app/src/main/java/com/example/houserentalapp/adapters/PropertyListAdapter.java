package com.example.houserentalapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.ImageUtils;

import java.util.List;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.ViewHolder> {

    private final Context context;
    private final List<Property> properties;
    private final PropertyCardAdapter.OnPropertyClickListener listener;

    public PropertyListAdapter(Context context, List<Property> properties, PropertyCardAdapter.OnPropertyClickListener listener) {
        this.context = context;
        this.properties = properties;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property_card_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = properties.get(position);

        holder.tvTitle.setText(property.getTitle());
        holder.tvLocation.setText(property.getFullLocation());
        holder.tvPrice.setText(CurrencyUtils.formatPerMonth(property.getPrice(), property.getCurrency()));
        
        ImageUtils.loadThumbnail(context, property.getCoverImage(), holder.ivPropertyImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPropertyClick(property);
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
        ImageView ivPropertyImage;
        TextView tvTitle, tvLocation, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPropertyImage = itemView.findViewById(R.id.iv_property_image);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
