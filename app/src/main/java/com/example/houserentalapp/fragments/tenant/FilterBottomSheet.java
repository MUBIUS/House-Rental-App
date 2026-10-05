package com.example.houserentalapp.fragments.tenant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.houserentalapp.R;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private ChipGroup cgPropertyType, cgBedrooms, cgFurnished;
    private RangeSlider rsPrice;
    private TextView tvMinPrice, tvMaxPrice, tvClearFilters;
    private AppCompatButton btnApply;

    private FilterListener listener;
    private String userCurrency;

    public interface FilterListener {
        void onFiltersApplied(String type, int minPrice, int maxPrice, String beds, String furnished);
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_filter, container, false);

        SessionManager session = new SessionManager(requireContext());
        userCurrency = session.getCurrency();

        bindViews(view);
        setupPropertyTypeChips();
        setListeners();

        return view;
    }

    private void bindViews(View view) {
        cgPropertyType = view.findViewById(R.id.cg_property_type);
        cgBedrooms = view.findViewById(R.id.cg_bedrooms);
        cgFurnished = view.findViewById(R.id.cg_furnished);
        rsPrice = view.findViewById(R.id.rs_price);
        tvMinPrice = view.findViewById(R.id.tv_min_price);
        tvMaxPrice = view.findViewById(R.id.tv_max_price);
        tvClearFilters = view.findViewById(R.id.tv_clear_filters);
        btnApply = view.findViewById(R.id.btn_apply_filters);
        
        updatePriceLabels(0f, 50000f);
    }

    private void setupPropertyTypeChips() {
        String[] types = {"Any", Constants.TYPE_HOME, Constants.TYPE_FLAT, Constants.TYPE_ROOM, 
                          Constants.TYPE_STUDIO, Constants.TYPE_VILLA};
        
        for (int i = 0; i < types.length; i++) {
            Chip chip = new Chip(requireContext());
            chip.setText(types[i]);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setChipStrokeColorResource(R.color.primary);
            chip.setChipStrokeWidth(1f);
            if (i == 0) chip.setChecked(true);
            cgPropertyType.addView(chip);
        }
    }

    private void setListeners() {
        rsPrice.addOnChangeListener((slider, value, fromUser) -> {
            updatePriceLabels(slider.getValues().get(0), slider.getValues().get(1));
        });

        tvClearFilters.setOnClickListener(v -> {
            ((Chip) cgPropertyType.getChildAt(0)).setChecked(true);
            cgBedrooms.check(R.id.chip_bed_any);
            cgFurnished.check(R.id.chip_furn_any);
            rsPrice.setValues(0f, 50000f);
        });

        btnApply.setOnClickListener(v -> {
            if (listener != null) {
                String type = getSelectedChipText(cgPropertyType);
                String beds = getSelectedChipText(cgBedrooms);
                String furnished = getSelectedChipText(cgFurnished);
                int minPrice = rsPrice.getValues().get(0).intValue();
                int maxPrice = rsPrice.getValues().get(1).intValue();

                listener.onFiltersApplied(type, minPrice, maxPrice, beds, furnished);
            }
            dismiss();
        });
    }

    private void updatePriceLabels(float min, float max) {
        tvMinPrice.setText(CurrencyUtils.format(min, userCurrency));
        String maxText = CurrencyUtils.format(max, userCurrency);
        if (max >= 100000) maxText += "+";
        tvMaxPrice.setText(maxText);
    }

    private String getSelectedChipText(ChipGroup group) {
        int id = group.getCheckedChipId();
        if (id != View.NO_ID) {
            Chip chip = group.findViewById(id);
            if (chip != null && !"Any".equals(chip.getText().toString())) {
                return chip.getText().toString();
            }
        }
        return null;
    }
}
