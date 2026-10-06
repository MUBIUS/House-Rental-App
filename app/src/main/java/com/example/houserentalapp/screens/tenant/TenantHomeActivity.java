package com.example.houserentalapp.screens.tenant;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.houserentalapp.R;
import com.example.houserentalapp.fragments.tenant.FavouritesFragment;
import com.example.houserentalapp.fragments.tenant.HomeFragment;
import com.example.houserentalapp.fragments.tenant.MyRentalsFragment;
import com.example.houserentalapp.fragments.tenant.ProfileFragment;
import com.example.houserentalapp.fragments.tenant.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TenantHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        // Note: DummyDataSeeder removed from production. Data seeding is debug-only.

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_favourites) {
                selectedFragment = new FavouritesFragment();
            } else if (itemId == R.id.nav_rentals) {
                selectedFragment = new MyRentalsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    public void switchToSearchTab() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_search);
        }
    }
}
