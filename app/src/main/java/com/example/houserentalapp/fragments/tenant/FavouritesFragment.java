package com.example.houserentalapp.fragments.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class FavouritesFragment extends Fragment implements PropertyCardAdapter.OnPropertyClickListener {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvFavourites;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    private FavouriteRepository favouriteRepository;
    private PropertyRepository propertyRepository;
    private SessionManager session;
    private PropertyCardAdapter adapter;

    private List<Property> favouriteProperties = new ArrayList<>();
    private String currentUid;

    public FavouritesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        favouriteRepository = new FavouriteRepository();
        propertyRepository = new PropertyRepository();
        session = new SessionManager(requireContext());
        currentUid = session.getUid();

        bindViews(view);
        setupRecyclerView();
        
        if (currentUid != null) {
            loadFavourites();
        } else {
            showEmptyState();
        }

        return view;
    }

    private void bindViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        rvFavourites = view.findViewById(R.id.rv_favourites);
        progressBar = view.findViewById(R.id.progress_bar);
        emptyState = view.findViewById(R.id.empty_state);

        swipeRefresh.setOnRefreshListener(() -> {
            if (currentUid != null) loadFavourites();
            else swipeRefresh.setRefreshing(false);
        });
    }

    private void setupRecyclerView() {
        rvFavourites.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PropertyCardAdapter(getContext(), new ArrayList<>(), this);
        rvFavourites.setAdapter(adapter);
    }

    private void loadFavourites() {
        showLoading();
        favouriteRepository.getFavouritePropertyIds(currentUid, new FavouriteRepository.FavouriteListCallback() {
            @Override
            public void onSuccess(List<String> propertyIds) {
                if (!isAdded() || getContext() == null) return;
                if (propertyIds.isEmpty()) {
                    showEmptyState();
                    return;
                }

                favouriteProperties.clear();
                int[] loadedCount = {0};

                for (String pId : propertyIds) {
                    propertyRepository.getProperty(pId, new PropertyRepository.PropertyCallback() {
                        @Override
                        public void onSuccess(Property property) {
                            favouriteProperties.add(property);
                            checkCompletion(loadedCount, propertyIds.size());
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Property might be deleted, just increment counter
                            checkCompletion(loadedCount, propertyIds.size());
                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded() || getContext() == null) return;
                showEmptyState();
                Toast.makeText(getContext(), "Failed to load favourites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private synchronized void checkCompletion(int[] count, int total) {
        count[0]++;
        if (count[0] >= total) {
            if (getActivity() != null && isAdded()) {
                getActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    if (favouriteProperties.isEmpty()) {
                        showEmptyState();
                    } else {
                        showResults();
                    }
                });
            }
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        rvFavourites.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        rvFavourites.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    private void showResults() {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        emptyState.setVisibility(View.GONE);
        rvFavourites.setVisibility(View.VISIBLE);
        adapter.updateData(favouriteProperties);
    }

    @Override
    public void onPropertyClick(Property property) {
        Intent intent = new Intent(requireContext(), PropertyDetailActivity.class);
        intent.putExtra(Constants.EXTRA_PROPERTY_ID, property.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Property property, ImageView favoriteIcon) {
        if (currentUid == null) return;
        // Remove from favourites immediately in UI, then update backend
        favouriteRepository.removeFavourite(currentUid, property.getId(), new FavouriteRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded() || getContext() == null) return;
                favouriteProperties.remove(property);
                adapter.updateData(favouriteProperties);
                if (favouriteProperties.isEmpty()) showEmptyState();
                Toast.makeText(getContext(), "Removed from Favourites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded() || getContext() == null) return;
                Toast.makeText(getContext(), "Failed to remove", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
