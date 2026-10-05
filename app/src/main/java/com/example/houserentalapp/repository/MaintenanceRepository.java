package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.MaintenanceTicket;
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

public class MaintenanceRepository {

    public interface TicketCallback {
        void onSuccess(String ticketId);
        void onFailure(String error);
    }

    public interface TicketListCallback {
        void onSuccess(List<MaintenanceTicket> tickets);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference maintenanceRef;

    public MaintenanceRepository() {
        maintenanceRef = FirebaseDatabase.getInstance().getReference(Constants.DB_MAINTENANCE);
    }

    public void submitTicket(MaintenanceTicket ticket, TicketCallback callback) {
        String id = maintenanceRef.push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        ticket.setId(id);
        maintenanceRef.child(id).setValue(ticket)
                .addOnSuccessListener(unused -> callback.onSuccess(id))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getTenantTickets(String tenantId, TicketListCallback callback) {
        maintenanceRef.orderByChild("tenantId").equalTo(tenantId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getLandlordTickets(String landlordId, TicketListCallback callback) {
        maintenanceRef.orderByChild("landlordId").equalTo(landlordId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void updateStatus(String ticketId, String status, String notes, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", System.currentTimeMillis());
        if (notes != null && !notes.isEmpty()) {
            updates.put("landlordNotes", notes);
        }
        if ("resolved".equals(status) || "closed".equals(status)) {
            updates.put("resolvedAt", System.currentTimeMillis());
        }
        maintenanceRef.child(ticketId).updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private ValueEventListener listenerFor(TicketListCallback callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MaintenanceTicket> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MaintenanceTicket t = ds.getValue(MaintenanceTicket.class);
                    if (t != null) { t.setId(ds.getKey()); list.add(t); }
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
