package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Visit;
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

public class VisitRepository {

    public interface VisitCallback {
        void onSuccess(String visitId);
        void onFailure(String error);
    }

    public interface VisitListCallback {
        void onSuccess(List<Visit> visits);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference visitsRef;

    public VisitRepository() {
        visitsRef = FirebaseDatabase.getInstance().getReference(Constants.DB_VISITS);
    }

    public void scheduleVisit(Visit visit, VisitCallback callback) {
        String id = visitsRef.push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        visit.setId(id);
        visitsRef.child(id).setValue(visit)
                .addOnSuccessListener(unused -> callback.onSuccess(id))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getTenantVisits(String tenantId, VisitListCallback callback) {
        visitsRef.orderByChild("tenantId").equalTo(tenantId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getLandlordVisits(String landlordId, VisitListCallback callback) {
        visitsRef.orderByChild("landlordId").equalTo(landlordId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void updateStatus(String visitId, String status, String notes, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", System.currentTimeMillis());
        if (notes != null && !notes.isEmpty()) {
            updates.put("landlordNotes", notes);
        }
        visitsRef.child(visitId).updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private ValueEventListener listenerFor(VisitListCallback callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Visit> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Visit v = ds.getValue(Visit.class);
                    if (v != null) { v.setId(ds.getKey()); list.add(v); }
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
