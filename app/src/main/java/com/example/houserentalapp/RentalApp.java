package com.example.houserentalapp;

import android.app.Application;

import com.example.houserentalapp.BuildConfig;
import com.example.houserentalapp.utils.DummyDataSeeder;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class RentalApp extends Application {

    private static RentalApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Enable disk persistence for offline support
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Seed dummy property data only in debug builds
        if (BuildConfig.DEBUG) {
            DummyDataSeeder.seedData();
        }
    }

    public static RentalApp getInstance() {
        return instance;
    }
}
