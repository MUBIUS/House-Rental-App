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
import com.example.houserentalapp.adapters.ApplicationCardAdapter;
import com.example.houserentalapp.model.RentalApplication;
import com.example.houserentalapp.repository.ApplicationRepository;
import com.example.houserentalapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsFragment extends Fragment {

    private ApplicationRepository applicationRepository;
    private SessionManager session;
    private ApplicationCardAdapter adapter;

    private ProgressBar progressBar;
    private RecyclerView rvApplications;
    private View layoutEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        rvApplications = view.findViewById(R.id.rv_applications);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        applicationRepository = new ApplicationRepository();
        session = new SessionManager(requireContext());

        setupRecyclerView();
        loadApplications();
    }

    private void setupRecyclerView() {
        adapter = new ApplicationCardAdapter(requireContext(), new ArrayList<>(),
                (application, position) -> withdrawApplication(application, position));
        rvApplications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvApplications.setAdapter(adapter);
    }

    private void loadApplications() {
        String uid = session.getUid();
        if (uid == null) {
            showEmpty();
            return;
        }

        showLoading();

        applicationRepository.getTenantApplications(uid, new ApplicationRepository.ApplicationListCallback() {
            @Override
            public void onSuccess(List<RentalApplication> applications) {
                if (!isAdded()) return;
                if (applications.isEmpty()) {
                    showEmpty();
                } else {
                    // Sort by most recently applied first
                    applications.sort((a, b) -> Long.compare(b.getAppliedAt(), a.getAppliedAt()));
                    adapter.updateData(applications);
                    showList();
                }
            }

            @Override
            public void onFailure(String error) {
                if (!isAdded()) return;
                showEmpty();
                Toast.makeText(requireContext(), "Failed to load applications: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void withdrawApplication(RentalApplication application, int position) {
        applicationRepository.updateStatus(application.getId(),
                com.example.houserentalapp.utils.Constants.APP_STATUS_WITHDRAWN,
                new ApplicationRepository.SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        if (!isAdded()) return;
                        application.setStatus(com.example.houserentalapp.utils.Constants.APP_STATUS_WITHDRAWN);
                        adapter.notifyItemChanged(position);
                        Toast.makeText(requireContext(), "Application withdrawn", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String error) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Failed to withdraw: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** Call this from outside (e.g. after a new application is submitted) to refresh the list. */
    public void refresh() {
        loadApplications();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvApplications.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showList() {
        progressBar.setVisibility(View.GONE);
        rvApplications.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        rvApplications.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
    }
}
