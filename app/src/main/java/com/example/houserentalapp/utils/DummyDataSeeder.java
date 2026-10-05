package com.example.houserentalapp.utils;

import android.util.Log;
import com.example.houserentalapp.model.Property;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.UUID;

public class DummyDataSeeder {
    private static final String TAG = "DummyDataSeeder";

    public static void seedData() {
        DatabaseReference propertiesRef = FirebaseDatabase.getInstance().getReference(Constants.DB_PROPERTIES);
        
        Log.d(TAG, "Checking database for existing properties...");
        propertiesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists() || task.getResult().getChildrenCount() == 0) {
                    Log.d(TAG, "Database empty. Seeding dummy data...");
                    seedProperties(propertiesRef);
                } else {
                    Log.d(TAG, "Database already has data. Found " + task.getResult().getChildrenCount() + " properties.");
                }
            } else {
                Log.e(TAG, "FAILED to access database. Is it enabled? Error: " + 
                        (task.getException() != null ? task.getException().getMessage() : "Unknown"));
            }
        });
    }

    private static void seedProperties(DatabaseReference propertiesRef) {
        // Property 1 - Featured Villa
        Property p1 = createDummyProperty("Luxury Oceanview Villa", 
                "A beautiful 4-bedroom villa with a stunning view of the ocean.", 
                Constants.TYPE_VILLA, 150000, "Mumbai", true);
        
        // Property 2 - Featured Flat
        Property p2 = createDummyProperty("Modern Downtown Flat", 
                "Sleek 2-bedroom apartment right in the city center.", 
                Constants.TYPE_FLAT, 45000, "Bangalore", true);

        // Property 3 - Standard Studio
        Property p3 = createDummyProperty("Cozy Studio Room", 
                "Affordable studio perfect for students.", 
                Constants.TYPE_STUDIO, 12000, "Pune", false);

        propertiesRef.child(p1.getId()).setValue(p1).addOnSuccessListener(a -> Log.d(TAG, "P1 uploaded"));
        propertiesRef.child(p2.getId()).setValue(p2).addOnSuccessListener(a -> Log.d(TAG, "P2 uploaded"));
        propertiesRef.child(p3.getId()).setValue(p3).addOnSuccessListener(a -> Log.d(TAG, "P3 uploaded"));
    }

    private static Property createDummyProperty(String title, String desc, String type, 
                                              int price, String city, boolean isFeatured) {
        Property p = new Property();
        p.setId(UUID.randomUUID().toString());
        p.setLandlordId("dummy_landlord_123");
        p.setLandlordName("John Doe Properties");
        p.setTitle(title);
        p.setDescription(desc);
        p.setPropertyType(type);
        p.setPrice(price);
        p.setCurrency("INR");
        p.setCity(city);
        p.setAddress("Dummy Address, " + city);
        p.setBedrooms(isFeatured ? 4 : 1);
        p.setBathrooms(isFeatured ? 3 : 1);
        p.setArea(isFeatured ? 2500 : 500);
        p.setCoverImage("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800");
        p.setStatus("active");
        p.setFeatured(isFeatured);
        p.setVerified(true);
        p.setAmenities(Arrays.asList("WiFi", "AC", "Parking"));
        return p;
    }
}
