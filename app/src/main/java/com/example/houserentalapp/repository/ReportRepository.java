package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Report;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportRepository {

    public interface ReportListCallback {
        void onSuccess(List<Report> reports);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference reportsRef;

    public ReportRepository() {
        reportsRef = FirebaseDatabase.getInstance().getReference(Constants.DB_REPORTS);
    }

    public void submitReport(Report report, SimpleCallback callback) {
        String id = reportsRef.push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        report.setId(id);
        reportsRef.child(id).setValue(report)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getPendingReports(ReportListCallback callback) {
        reportsRef.orderByChild("status").equalTo("pending")
                .addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void getAllReports(ReportListCallback callback) {
        reportsRef.addListenerForSingleValueEvent(listenerFor(callback));
    }

    public void updateStatus(String reportId, String status, SimpleCallback callback) {
        reportsRef.child(reportId).child("status").setValue(status)
                .addOnSuccessListener(unused -> {
                    reportsRef.child(reportId).child("updatedAt").setValue(System.currentTimeMillis());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    private ValueEventListener listenerFor(ReportListCallback callback) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Report> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Report r = ds.getValue(Report.class);
                    if (r != null) { r.setId(ds.getKey()); list.add(r); }
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
