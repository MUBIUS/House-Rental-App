package com.example.houserentalapp.fragments.tenant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.houserentalapp.R;
import com.example.houserentalapp.adapters.LeaseCardAdapter;
import com.example.houserentalapp.model.Lease;
import com.example.houserentalapp.repository.LeaseRepository;
import com.example.houserentalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class LeasesFragment extends Fragment {

    private LeaseRepository leaseRepository;
    private SessionManager session;
    private LeaseCardAdapter adapter;

    private ProgressBar progressBar;
    private RecyclerView rvLeases;
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        rvLeases = view.findViewById(R.id.rv_leases);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        leaseRepository = new LeaseRepository();
        session = new SessionManager(requireContext());

        setupRecyclerView();
        loadLeases();
    }

    private void setupRecyclerView() {
        adapter = new LeaseCardAdapter(requireContext(), new ArrayList<>());
        rvLeases.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLeases.setAdapter(adapter);
    }

    private void loadLeases() {
        String uid = session.getUid();
        if (uid == null) {
            showEmpty();
            return;
        }

        showLoading();

        leaseRepository.getTenantLeases(uid, new LeaseRepository.LeaseListCallback() {
            @Override
            public void onSuccess(List<Lease> leases) {
                if (!isAdded()) return;
                if (leases.isEmpty()) {
                    showEmpty();
                } else {
                    // Sort active first, then by start date descending
                    leases.sort((a, b) -> {
                        if (a.isActive() && !b.isActive()) return -1;
                        if (!a.isActive() && b.isActive()) return 1;
                        return Long.compare(b.getStartDate(), a.getStartDate());
                    });
                    adapter.updateData(leases);
                    showList();
                }
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded()) return;
                showEmpty();
                Toast.makeText(requireContext(), "Failed to load leases: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvLeases.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showList() {
        progressBar.setVisibility(View.GONE);
        rvLeases.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        rvLeases.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}
