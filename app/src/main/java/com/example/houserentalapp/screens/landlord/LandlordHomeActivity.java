package com.example.houserentalapp.screens.landlord;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.houserentalapp.R;
import com.example.houserentalapp.fragments.tenant.ApplicationsFragment;
import com.example.houserentalapp.fragments.tenant.HomeFragment;
import com.example.houserentalapp.fragments.tenant.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandlordHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_menu_landlord);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard || itemId == R.id.nav_properties) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_applications) {
                selectedFragment = new ApplicationsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else {
                selectedFragment = new HomeFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_properties);
        }
    }
}
