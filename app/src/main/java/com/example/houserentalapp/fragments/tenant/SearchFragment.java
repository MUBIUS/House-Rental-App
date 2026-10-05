package com.example.houserentalapp.fragments.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalapp.R;
import com.example.houserentalapp.adapters.PropertyCardAdapter;
import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.repository.FavouriteRepository;
import com.example.houserentalapp.repository.PropertyRepository;
import com.example.houserentalapp.screens.tenant.PropertyDetailActivity;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements PropertyCardAdapter.OnPropertyClickListener {

    private EditText etSearch;
    private ImageView ivClear, btnFilter;
    private RecyclerView rvSearchResults;
    private ProgressBar progressBar;
    private LinearLayout layoutInitialState;
    private View emptyState;

    private PropertyRepository propertyRepository;
    private FavouriteRepository favouriteRepository;
    private SessionManager session;
    private PropertyCardAdapter searchAdapter;
    private List<Property> searchResults = new ArrayList<>();

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        propertyRepository = new PropertyRepository();
        favouriteRepository = new FavouriteRepository();
        session = new SessionManager(requireContext());

        bindViews(view);
        setupRecyclerView();
        setListeners();

        return view;
    }

    private void bindViews(View view) {
        etSearch = view.findViewById(R.id.et_search);
        ivClear = view.findViewById(R.id.iv_clear);
        btnFilter = view.findViewById(R.id.btn_filter);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutInitialState = view.findViewById(R.id.layout_initial_state);
        emptyState = view.findViewById(R.id.empty_state);
    }

    private void setupRecyclerView() {
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new PropertyCardAdapter(getContext(), new ArrayList<>(), this);
        rvSearchResults.setAdapter(searchAdapter);
    }

    private void setListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                if (s.length() == 0) {
                    showInitialState();
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        ivClear.setOnClickListener(v -> etSearch.setText(""));

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        btnFilter.setOnClickListener(v -> {
            FilterBottomSheet filterSheet = new FilterBottomSheet();
            filterSheet.setFilterListener((type, minPrice, maxPrice, beds, furnished) -> {
                // Apply filters to search results
                applyFilters(type, minPrice, maxPrice, beds, furnished);
            });
            filterSheet.show(getChildFragmentManager(), "FilterBottomSheet");
        });
    }

    private void applyFilters(String type, int minPrice, int maxPrice, String beds, String furnished) {
        showLoading();
        propertyRepository.getActiveProperties(new PropertyRepository.PropertyListCallback() {
            @Override
            public void onSuccess(List<Property> properties) {
                if (!isAdded() || getContext() == null) return;
                searchResults.clear();
                for (Property p : properties) {
                    boolean match = true;
                    if (type != null && !type.equals(p.getPropertyType())) match = false;
                    if (p.getPrice() < minPrice || p.getPrice() > maxPrice) match = false;
                    if (beds != null) {
                        if (beds.equals("4+") && p.getBedrooms() < 4) match = false;
                        else if (!beds.equals("4+") && p.getBedrooms() != Integer.parseInt(beds)) match = false;
                    }
                    if (furnished != null && !furnished.equals(p.getFurnished())) match = false;

                    if (match) searchResults.add(p);
                }
                showResults(searchResults);
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded() || getContext() == null) return;
                showInitialState();
            }
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) return;

        showLoading();
        // Client-side simple search implementation
        propertyRepository.getActiveProperties(new PropertyRepository.PropertyListCallback() {
            @Override
            public void onSuccess(List<Property> properties) {
                if (!isAdded() || getContext() == null) return;
                searchResults.clear();
                String lowerQuery = query.toLowerCase();

                for (Property p : properties) {
                    if ((p.getTitle() != null && p.getTitle().toLowerCase().contains(lowerQuery)) ||
                        (p.getCity() != null && p.getCity().toLowerCase().contains(lowerQuery)) ||
                        (p.getFullLocation() != null && p.getFullLocation().toLowerCase().contains(lowerQuery))) {
                        searchResults.add(p);
                    }
                }

                showResults(searchResults);
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded() || getContext() == null) return;
                showInitialState();
            }
        });
    }

    private void showLoading() {
        layoutInitialState.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showInitialState() {
        progressBar.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        layoutInitialState.setVisibility(View.VISIBLE);
    }

    private void showResults(List<Property> results) {
        progressBar.setVisibility(View.GONE);
        layoutInitialState.setVisibility(View.GONE);

        if (results.isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            searchAdapter.updateData(results);
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
