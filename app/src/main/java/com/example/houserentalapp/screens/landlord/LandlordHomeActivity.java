package com.example.houserentalapp.screens.landlord;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LandlordHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Will use the tenant home layout for now until we build the landlord one
        setContentView(R.layout.activity_tenant_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_menu_landlord);
        
        // Placeholder for future routing
    }
}
