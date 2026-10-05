package com.example.houserentalapp.screens.tenant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.houserentalapp.R;
import com.example.houserentalapp.adapters.ImageSliderAdapter;
import com.example.houserentalapp.adapters.ReviewAdapter;
import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.model.Review;
import com.example.houserentalapp.model.User;
import com.example.houserentalapp.repository.ApplicationRepository;
import com.example.houserentalapp.repository.PropertyRepository;
import com.example.houserentalapp.repository.ReviewRepository;
import com.example.houserentalapp.repository.UserRepository;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.ImageUtils;
import com.example.houserentalapp.utils.SessionManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PropertyDetailActivity extends AppCompatActivity {

    private PropertyRepository propertyRepository;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private ApplicationRepository applicationRepository;
    private SessionManager session;

    private String propertyId;
    private Property currentProperty;
    private boolean alreadyApplied = false;

    // UI Elements
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;
    
    private ViewPager2 vpImageSlider;
    private TextView tvTitle, tvLocation, tvPrice, tvPropertyType, tvStatus, tvRating;
    private TextView tvBeds, tvBaths, tvArea, tvDescription;
    private ChipGroup cgAmenities;
    
    // Landlord info
    private CircleImageView ivLandlord;
    private TextView tvLandlordName;
    private View btnChat;
    
    // Reviews
    private RecyclerView rvReviews;
    private TextView tvNoReviews;
    private ReviewAdapter reviewAdapter;
    
    // Bottom Bar
    private TextView tvBottomPrice;
    private AppCompatButton btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        propertyId = getIntent().getStringExtra(Constants.EXTRA_PROPERTY_ID);
        if (propertyId == null) {
            Toast.makeText(this, "Property ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        propertyRepository = new PropertyRepository();
        userRepository = new UserRepository();
        reviewRepository = new ReviewRepository();
        applicationRepository = new ApplicationRepository();
        session = new SessionManager(this);

        bindViews();
        setupToolbar();
        setupRecyclerView();

        loadPropertyData();

        // Increment view count when details page is opened
        propertyRepository.incrementViewCount(propertyId);

        // Check if the tenant already has an active application
        if (session.getUid() != null) {
            applicationRepository.checkExistingApplication(
                    session.getUid(), propertyId,
                    new UserRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() { /* no existing application */ }

                        @Override
                        public void onFailure(String errorMessage) {
                            alreadyApplied = true;
                            markApplyButtonApplied();
                        }
                    });
        }
    }

    private void bindViews() {
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        
        vpImageSlider = findViewById(R.id.vp_image_slider);
        tvTitle = findViewById(R.id.tv_title);
        tvLocation = findViewById(R.id.tv_location);
        tvPrice = findViewById(R.id.tv_price);
        tvPropertyType = findViewById(R.id.tv_property_type);
        tvStatus = findViewById(R.id.tv_status);
        tvRating = findViewById(R.id.tv_rating);
        
        tvBeds = findViewById(R.id.tv_beds);
        tvBaths = findViewById(R.id.tv_baths);
        tvArea = findViewById(R.id.tv_area);
        tvDescription = findViewById(R.id.tv_description);
        cgAmenities = findViewById(R.id.cg_amenities);
        
        ivLandlord = findViewById(R.id.iv_landlord);
        tvLandlordName = findViewById(R.id.tv_landlord_name);
        btnChat = findViewById(R.id.btn_chat);
        
        rvReviews = findViewById(R.id.rv_reviews);
        tvNoReviews = findViewById(R.id.tv_no_reviews);
        
        tvBottomPrice = findViewById(R.id.tv_bottom_price);
        btnApply = findViewById(R.id.btn_apply);
        
        btnApply.setOnClickListener(v -> handleApplyAction());
        btnChat.setOnClickListener(v -> handleChatAction());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                if (currentProperty != null) collapsingToolbar.setTitle(currentProperty.getTitle());
            } else {
                collapsingToolbar.setTitle("");
            }
        });
    }

    private void setupRecyclerView() {
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadPropertyData() {
        propertyRepository.getProperty(propertyId, new PropertyRepository.PropertyCallback() {
            @Override
            public void onSuccess(Property property) {
                currentProperty = property;
                populatePropertyUI(property);
                loadLandlordData(property.getLandlordId());
                loadReviews();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(PropertyDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populatePropertyUI(Property property) {
        tvTitle.setText(property.getTitle());
        tvLocation.setText(property.getFullLocation());
        tvDescription.setText(property.getDescription());
        
        tvPropertyType.setText(property.getPropertyType());
        tvPropertyType.setBackgroundResource(R.drawable.bg_badge_info);
        tvPropertyType.setTextColor(ContextCompat.getColor(this, R.color.info_blue));
        
        String priceFormatted = CurrencyUtils.formatPerMonth(property.getPrice(), property.getCurrency());
        tvBottomPrice.setText(priceFormatted);

        tvBeds.setText(property.getBedrooms() + " Beds");
        tvBaths.setText(property.getBathrooms() + " Baths");
        tvArea.setText((int)property.getArea() + " sqft");
        
        if ("active".equals(property.getStatus())) {
            tvStatus.setText("Available");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_active));
            tvStatus.setBackgroundResource(R.drawable.bg_badge_active);
        } else {
            tvStatus.setText("Not Available");
            tvStatus.setTextColor(ContextCompat.getColor(this, R.color.status_rejected));
            tvStatus.setBackgroundResource(R.drawable.bg_badge_rejected);
            btnApply.setEnabled(false);
            btnApply.setText("Not Available");
        }

        if (property.getReviewCount() > 0) {
            tvRating.setText(String.format("%.1f (%d reviews)", property.getAverageRating(), property.getReviewCount()));
        } else {
            tvRating.setText("No ratings yet");
        }

        List<String> images = new ArrayList<>();
        if (property.getCoverImage() != null) images.add(property.getCoverImage());
        if (images.isEmpty()) images.add("placeholder");
        
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, images);
        vpImageSlider.setAdapter(sliderAdapter);

        cgAmenities.removeAllViews();
        if (property.getAmenities() != null) {
            for (String amenity : property.getAmenities()) {
                Chip chip = new Chip(this);
                chip.setText(amenity);
                chip.setChipBackgroundColorResource(R.color.surface_variant);
                cgAmenities.addView(chip);
            }
        }
    }

    private void loadLandlordData(String landlordId) {
        if (landlordId == null) return;
        userRepository.getUser(landlordId, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                tvLandlordName.setText(user.getName());
                if (user.getProfileImage() != null) {
                    ImageUtils.loadAvatar(PropertyDetailActivity.this, user.getProfileImage(), ivLandlord);
                }
            }

            @Override
            public void onFailure(String errorMessage) {}
        });
    }

    private void loadReviews() {
        reviewRepository.getPropertyReviews(propertyId, new ReviewRepository.ReviewListCallback() {
            @Override
            public void onSuccess(List<Review> reviews) {
                if (reviews.isEmpty()) {
                    rvReviews.setVisibility(View.GONE);
                    tvNoReviews.setVisibility(View.VISIBLE);
                } else {
                    rvReviews.setVisibility(View.VISIBLE);
                    tvNoReviews.setVisibility(View.GONE);
                    reviewAdapter.updateData(reviews);
                }
            }

            @Override
            public void onFailure(String error) {}
        });
    }

    private void handleApplyAction() {
        if (session.getUid() == null) {
            Toast.makeText(this, "Please login to apply", Toast.LENGTH_SHORT).show();
            return;
        }
        if (alreadyApplied) {
            Toast.makeText(this, "You have already applied for this property", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentProperty == null) return;

        ApplyBottomSheet sheet = ApplyBottomSheet.newInstance(currentProperty);
        sheet.setOnApplicationSubmittedListener(applicationId -> {
            alreadyApplied = true;
            markApplyButtonApplied();
        });
        sheet.show(getSupportFragmentManager(), "ApplyBottomSheet");
    }

    private void markApplyButtonApplied() {
        btnApply.setText("Applied ✓");
        btnApply.setEnabled(false);
        btnApply.setTextColor(Color.WHITE);
    }

    private void handleChatAction() {
        if (session.getUid() == null) {
            Toast.makeText(this, "Please login to chat", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentProperty == null) return;
        if (session.getUid().equals(currentProperty.getLandlordId())) {
            Toast.makeText(this, "You cannot message your own property listing", Toast.LENGTH_SHORT).show();
            return;
        }
        // TODO: Launch ChatActivity when implemented
        // Intent intent = new Intent(this, ChatActivity.class);
        // intent.putExtra(Constants.EXTRA_PROPERTY_ID, propertyId);
        // intent.putExtra(Constants.EXTRA_USER_ID, currentProperty.getLandlordId());
        // startActivity(intent);
        Toast.makeText(this, "Messaging is not available in this version", Toast.LENGTH_SHORT).show();
    }
}
