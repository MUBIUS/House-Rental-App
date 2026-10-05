package com.example.houserentalapp.screens.tenant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.houserentalapp.R;
import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.model.RentalApplication;
import com.example.houserentalapp.repository.ApplicationRepository;
import com.example.houserentalapp.repository.UserRepository;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ApplyBottomSheet extends BottomSheetDialogFragment {

    public interface OnApplicationSubmittedListener {
        void onApplicationSubmitted(String applicationId);
    }

    private Property property;
    private OnApplicationSubmittedListener listener;

    // Form layout
    private LinearLayout layoutForm;
    private LinearLayout layoutSuccess;

    private ImageView ivPropertyThumb;
    private TextView tvPropertyTitle, tvPropertyPrice, tvPropertyLocation;
    private TextInputEditText etMessage;
    private MaterialButton btnSubmit, btnCancel, btnDone;

    private ApplicationRepository applicationRepository;
    private SessionManager session;

    // ── Factory ───────────────────────────────────────────────
    public static ApplyBottomSheet newInstance(Property property) {
        ApplyBottomSheet sheet = new ApplyBottomSheet();
        sheet.property = property;
        return sheet;
    }

    public void setOnApplicationSubmittedListener(OnApplicationSubmittedListener listener) {
        this.listener = listener;
    }

    // ── Lifecycle ─────────────────────────────────────────────
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_apply, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        applicationRepository = new ApplicationRepository();
        session = new SessionManager(requireContext());

        // Bind views
        layoutForm = view.findViewById(R.id.layout_form);
        layoutSuccess = view.findViewById(R.id.layout_success);
        ivPropertyThumb = view.findViewById(R.id.iv_property_thumb);
        tvPropertyTitle = view.findViewById(R.id.tv_property_title);
        tvPropertyPrice = view.findViewById(R.id.tv_property_price);
        tvPropertyLocation = view.findViewById(R.id.tv_property_location);
        etMessage = view.findViewById(R.id.et_message);
        btnSubmit = view.findViewById(R.id.btn_submit);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnDone = view.findViewById(R.id.btn_done);

        populatePropertySummary();
        setupListeners();
    }

    // ── UI helpers ────────────────────────────────────────────
    private void populatePropertySummary() {
        if (property == null) return;

        tvPropertyTitle.setText(property.getTitle());
        tvPropertyPrice.setText(CurrencyUtils.formatPerMonth(property.getPrice(), property.getCurrency()));
        tvPropertyLocation.setText(property.getFullLocation());

        if (property.getCoverImage() != null && !property.getCoverImage().isEmpty()) {
            Glide.with(requireContext())
                    .load(property.getCoverImage())
                    .placeholder(R.drawable.placeholder_property)
                    .centerCrop()
                    .into(ivPropertyThumb);
        }
    }

    private void setupListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnDone.setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(v -> checkAndSubmit());
    }

    // ── Application logic ─────────────────────────────────────
    private void checkAndSubmit() {
        String uid = session.getUid();
        if (uid == null) {
            Toast.makeText(requireContext(), "Please log in to apply", Toast.LENGTH_SHORT).show();
            return;
        }

        setSubmitLoading(true);

        // First check if the tenant already has an active application for this property
        applicationRepository.checkExistingApplication(uid, property.getId(),
                new UserRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        // No duplicate — proceed with submission
                        submitApplication(uid);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        if (!isAdded()) return;
                        setSubmitLoading(false);
                        Toast.makeText(requireContext(),
                                "You have already applied for this property",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitApplication(String tenantId) {
        String message = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        String tenantName = session.getName();

        RentalApplication application = new RentalApplication(
                property.getId(),
                property.getTitle(),
                property.getCoverImage(),
                tenantId,
                tenantName,
                property.getLandlordId(),
                message
        );

        applicationRepository.submitApplication(application, new ApplicationRepository.ApplicationCallback() {
            @Override
            public void onSuccess(String applicationId) {
                if (!isAdded()) return;
                setSubmitLoading(false);
                showSuccessState();
                if (listener != null) {
                    listener.onApplicationSubmitted(applicationId);
                }
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded()) return;
                setSubmitLoading(false);
                Toast.makeText(requireContext(), "Failed to submit: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── State helpers ──────────────────────────────────────────
    private void setSubmitLoading(boolean loading) {
        btnSubmit.setEnabled(!loading);
        btnCancel.setEnabled(!loading);
        btnSubmit.setText(loading ? "Submitting…" : "Submit Application");
    }

    private void showSuccessState() {
        layoutForm.setVisibility(View.GONE);
        layoutSuccess.setVisibility(View.VISIBLE);
    }
}
