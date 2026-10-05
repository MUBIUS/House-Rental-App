package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Lease;
import com.example.houserentalapp.model.Payment;
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

public class LeaseRepository {

    public interface LeaseCallback {
        void onSuccess(String leaseId);
        void onFailure(String error);
    }

    public interface LeaseListCallback {
        void onSuccess(List<Lease> leases);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference leasesRef;
    private final DatabaseReference paymentsRef;

    public LeaseRepository() {
        leasesRef = FirebaseDatabase.getInstance().getReference(Constants.DB_LEASES);
        paymentsRef = FirebaseDatabase.getInstance().getReference(Constants.DB_PAYMENTS);
    }

    public void createLease(Lease lease, LeaseCallback callback) {
        String id = leasesRef.push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        lease.setId(id);
        leasesRef.child(id).setValue(lease)
                .addOnSuccessListener(unused -> callback.onSuccess(id))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getTenantLeases(String tenantId, LeaseListCallback callback) {
        leasesRef.orderByChild("tenantId").equalTo(tenantId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getLandlordLeases(String landlordId, LeaseListCallback callback) {
        leasesRef.orderByChild("landlordId").equalTo(landlordId)
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getLease(String leaseId, LeaseListCallback callback) {
        leasesRef.child(leaseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lease> list = new ArrayList<>();
                Lease l = snapshot.getValue(Lease.class);
                if (l != null) { l.setId(snapshot.getKey()); list.add(l); }
                callback.onSuccess(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void updateStatus(String leaseId, String status, SimpleCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("updatedAt", System.currentTimeMillis());
        leasesRef.child(leaseId).updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // ── Payments ──────────────────────────────────────────────

    public void createPayment(Payment payment, SimpleCallback callback) {
        String id = paymentsRef.child(payment.getLeaseId()).push().getKey();
        if (id == null) { callback.onFailure("Failed to generate payment ID"); return; }
        payment.setId(id);
        paymentsRef.child(payment.getLeaseId()).child(id).setValue(payment)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getLeasePayments(String leaseId, PaymentListCallback callback) {
        paymentsRef.child(leaseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Payment> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Payment p = ds.getValue(Payment.class);
                    if (p != null) { p.setId(ds.getKey()); list.add(p); }
                }
                callback.onSuccess(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void markPaymentPaid(String leaseId, String paymentId, String method,
                                 String transactionId, SimpleCallback callback) {
        // Use updateChildren for an atomic write — prevents partial-write data corruption
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "paid");
        updates.put("paidDate", System.currentTimeMillis());
        updates.put("method", method);
        updates.put("transactionId", transactionId);
        paymentsRef.child(leaseId).child(paymentId).updateChildren(updates)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface PaymentListCallback {
        void onSuccess(List<Payment> payments);
        void onFailure(String error);
    }

    private ValueEventListener listenerFor(LeaseListCallback callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Lease> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Lease l = ds.getValue(Lease.class);
                    if (l != null) { l.setId(ds.getKey()); list.add(l); }
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
