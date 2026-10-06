package com.example.houserentalapp.fragments.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.houserentalapp.R;
import com.example.houserentalapp.adapters.PropertyCardAdapter;
import com.example.houserentalapp.adapters.PropertyListAdapter;
import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.repository.FavouriteRepository;
import com.example.houserentalapp.repository.PropertyRepository;
import com.example.houserentalapp.screens.tenant.PropertyDetailActivity;
import com.example.houserentalapp.screens.tenant.TenantHomeActivity;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.ImageUtils;
import com.example.houserentalapp.utils.SessionManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements PropertyCardAdapter.OnPropertyClickListener {

    private ShimmerFrameLayout shimmerView;
    private LinearLayout contentLayout;
    private SwipeRefreshLayout swipeRefresh;
    
    private TextView tvGreeting;
    private CircleImageView ivProfile;
    private LinearLayout searchBarTrigger;
    
    private LinearLayout featuredSection;
    private ViewPager2 vpFeatured;
    private ChipGroup chipGroupCategories;
    private TextView tvListTitle;
    private RecyclerView rvProperties;
    private View emptyState;

    private PropertyRepository propertyRepository;
    private FavouriteRepository favouriteRepository;
    private SessionManager session;

    private PropertyCardAdapter mainAdapter;
    private PropertyListAdapter featuredAdapter;
    
    private List<Property> allPropertiesList = new ArrayList<>();
    private List<Property> featuredPropertiesList = new ArrayList<>();

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tenant_home, container, false);
        
        propertyRepository = new PropertyRepository();
        favouriteRepository = new FavouriteRepository();
        session = new SessionManager(requireContext());

        bindViews(view);
        setupAdapters();
        setupCategories();
        setListeners();
        
        loadProfileHeader();
        loadData();

        return view;
    }

    private void bindViews(View view) {
        shimmerView = view.findViewById(R.id.shimmer_view);
        contentLayout = view.findViewById(R.id.content_layout);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        ivProfile = view.findViewById(R.id.iv_profile);
        searchBarTrigger = view.findViewById(R.id.search_bar_trigger);
        featuredSection = view.findViewById(R.id.featured_section);
        vpFeatured = view.findViewById(R.id.vp_featured);
        chipGroupCategories = view.findViewById(R.id.chip_group_categories);
        tvListTitle = view.findViewById(R.id.tv_list_title);
        rvProperties = view.findViewById(R.id.rv_properties);
        emptyState = view.findViewById(R.id.empty_state);
        
        vpFeatured.setOffscreenPageLimit(3);
        rvProperties.setLayoutManager(new LinearLayoutManager(getContext()));
        rvProperties.setNestedScrollingEnabled(false);
    }

    private void setupAdapters() {
        mainAdapter = new PropertyCardAdapter(getContext(), new ArrayList<>(), this);
        rvProperties.setAdapter(mainAdapter);
        featuredAdapter = new PropertyListAdapter(getContext(), new ArrayList<>(), this);
        vpFeatured.setAdapter(featuredAdapter);
    }

    private void setupCategories() {
        if (!isAdded() || getContext() == null) return;
        String[] types = {Constants.TYPE_HOME, Constants.TYPE_FLAT, Constants.TYPE_ROOM, Constants.TYPE_STUDIO, Constants.TYPE_VILLA};
        chipGroupCategories.removeAllViews();
        for (String type : types) {
            Chip chip = new Chip(requireContext());
            chip.setId(View.generateViewId());
            chip.setText(type);
            chip.setCheckable(true);
            chipGroupCategories.addView(chip);
        }
    }

    private void setListeners() {
        if (searchBarTrigger != null) {
            searchBarTrigger.setOnClickListener(v -> {
                if (getActivity() instanceof TenantHomeActivity) {
                    ((TenantHomeActivity) getActivity()).switchToSearchTab();
                }
            });
        }
        swipeRefresh.setOnRefreshListener(this::loadData);
        chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds == null || checkedIds.isEmpty()) {
                mainAdapter.updateData(allPropertiesList);
                checkEmptyState(allPropertiesList.isEmpty());
            } else {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null && chip.getText() != null) {
                    filterByType(chip.getText().toString());
                } else {
                    mainAdapter.updateData(allPropertiesList);
                    checkEmptyState(allPropertiesList.isEmpty());
                }
            }
        });
    }

    private void loadProfileHeader() {
        String name = session.getName();
        if (name != null && !name.isEmpty()) tvGreeting.setText("Hi, " + name.split(" ")[0] + " 👋");
        String profileImage = session.getProfileImage();
        if (profileImage != null) ImageUtils.loadAvatar(requireContext(), profileImage, ivProfile);
    }

    private void loadData() {
        showLoading(true);
        
        // Safety timeout for data loading
        final boolean[] handled = {false};
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!handled[0]) {
                handled[0] = true;
                if (!isAdded() || getContext() == null) return;
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                checkEmptyState(true);
                Toast.makeText(getContext(), "Connection timeout. Please check your database.", Toast.LENGTH_LONG).show();
            }
        }, 8000);

        propertyRepository.getActiveProperties(new PropertyRepository.PropertyListCallback() {
            @Override
            public void onSuccess(List<Property> properties) {
                if (handled[0]) return;
                if (!isAdded() || getContext() == null) return;
                allPropertiesList = properties;
                mainAdapter.updateData(properties);
                checkEmptyState(properties.isEmpty());

                propertyRepository.getFeaturedProperties(new PropertyRepository.PropertyListCallback() {
                    @Override
                    public void onSuccess(List<Property> featured) {
                        if (handled[0]) return;
                        if (!isAdded() || getContext() == null) return;
                        handled[0] = true;
                        featuredPropertiesList = featured;
                        featuredAdapter.updateData(featured);
                        featuredSection.setVisibility(featured.isEmpty() ? View.GONE : View.VISIBLE);
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);
                    }
                    @Override public void onFailure(String error) { handleFailure(); }
                });
            }
            @Override public void onFailure(String errorMessage) { handleFailure(); }

            private void handleFailure() {
                if (handled[0]) return;
                handled[0] = true;
                if (!isAdded() || getContext() == null) return;
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                checkEmptyState(true);
            }
        });
    }

    private void filterByType(String type) {
        List<Property> filtered = new ArrayList<>();
        for (Property p : allPropertiesList) {
            if (type.equalsIgnoreCase(p.getPropertyType())) filtered.add(p);
        }
        mainAdapter.updateData(filtered);
        checkEmptyState(filtered.isEmpty());
    }

    private void checkEmptyState(boolean isEmpty) {
        rvProperties.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private void showLoading(boolean show) {
        if (show && !swipeRefresh.isRefreshing()) {
            shimmerView.startShimmer();
            shimmerView.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        } else {
            shimmerView.stopShimmer();
            shimmerView.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPropertyClick(Property property) {
        Intent intent = new Intent(requireContext(), PropertyDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_ID, property.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Property property, ImageView favoriteIcon) {
        String uid = session.getUid();
        if (uid == null) {
            Toast.makeText(getContext(), "Please login to add to favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        favouriteRepository.isFavourite(uid, property.getId(), new FavouriteRepository.FavouriteCheckCallback() {
            @Override
            public void onResult(boolean isFavourite) {
                if (isFavourite) {
                    favouriteRepository.removeFavourite(uid, property.getId(), new FavouriteRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            favoriteIcon.setImageResource(R.drawable.ic_empty_favorite);
                            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                        @Override public void onFailure(String error) {}
                    });
                } else {
                    favouriteRepository.addFavourite(uid, property.getId(), new FavouriteRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            favoriteIcon.setImageResource(R.drawable.ic_fill_favorite);
                            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        }
                        @Override public void onFailure(String error) {}
                    });
                }
            }
        });
    }
}
