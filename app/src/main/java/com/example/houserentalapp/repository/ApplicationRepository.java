package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.RentalApplication;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationRepository {

    public interface ApplicationCallback {
        void onSuccess(String applicationId);
        void onFailure(String error);
    }

    public interface ApplicationListCallback {
        void onSuccess(List<RentalApplication> applications);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference appsRef;

    public ApplicationRepository() {
        appsRef = FirebaseDatabase.getInstance().getReference(Constants.DB_APPLICATIONS);
    }

    public void submitApplication(RentalApplication application, ApplicationCallback callback) {
        String id = appsRef.push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        application.setId(id);
        appsRef.child(id).setValue(application)
                .addOnSuccessListener(unused -> callback.onSuccess(id))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getTenantApplications(String tenantId, ApplicationListCallback callback) {
        appsRef.orderByChild("tenantId").equalTo(tenantId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getLandlordApplications(String landlordId, ApplicationListCallback callback) {
        appsRef.orderByChild("landlordId").equalTo(landlordId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getPropertyApplications(String propertyId, ApplicationListCallback callback) {
        appsRef.orderByChild("propertyId").equalTo(propertyId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void updateStatus(String applicationId, String status, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", System.currentTimeMillis());
        appsRef.child(applicationId).updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void checkExistingApplication(String tenantId, String propertyId,
                                          UserRepository.SimpleCallback callback) {
        appsRef.orderByChild("tenantId").equalTo(tenantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            RentalApplication app = ds.getValue(RentalApplication.class);
                            if (app != null && propertyId.equals(app.getPropertyId())
                                    && !"withdrawn".equals(app.getStatus())
                                    && !"rejected".equals(app.getStatus())) {
                                callback.onFailure("Already applied");
                                return;
                            }
                        }
                        callback.onSuccess();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // On DB error, block submission to avoid bypassing the duplicate check
                        callback.onFailure("Could not verify application status. Please try again.");
                    }
                });
    }

    private ValueEventListener listenerFor(ApplicationListCallback callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RentalApplication> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    RentalApplication app = ds.getValue(RentalApplication.class);
                    if (app != null) { app.setId(ds.getKey()); list.add(app); }
                }
                callback.onSuccess(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        };
    }
}
