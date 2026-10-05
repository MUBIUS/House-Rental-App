package com.example.houserentalapp.fragments.tenant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.houserentalapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyRentalsFragment extends Fragment {

    private ApplicationsFragment applicationsFragment;
    private LeasesFragment leasesFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_rentals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        // Initialise child fragments so we can hold references
        applicationsFragment = new ApplicationsFragment();
        leasesFragment = new LeasesFragment();

        viewPager.setAdapter(new RentalsPageAdapter(requireActivity()));
        viewPager.setOffscreenPageLimit(1); // keep both tabs in memory

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Applications" : "Leases");
        }).attach();
    }

    /** Trigger a data refresh on the Applications tab (called after a new application is submitted). */
    public void refreshApplications() {
        if (applicationsFragment != null) {
            applicationsFragment.refresh();
        }
    }

    // ── Inner pager adapter ────────────────────────────────────
    private class RentalsPageAdapter extends FragmentStateAdapter {

        RentalsPageAdapter(@NonNull FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return position == 0 ? applicationsFragment : leasesFragment;
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
