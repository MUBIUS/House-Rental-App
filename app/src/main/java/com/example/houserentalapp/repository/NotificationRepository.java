package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Notification;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    public interface NotificationListCallback {
        void onSuccess(List<Notification> notifications);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference notificationsRef;

    public NotificationRepository() {
        notificationsRef = FirebaseDatabase.getInstance().getReference(Constants.DB_NOTIFICATIONS);
    }

    public void sendNotification(String targetUid, Notification notification,
                                  SimpleCallback callback) {
        String id = notificationsRef.child(targetUid).push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        notification.setId(id);
        notificationsRef.child(targetUid).child(id).setValue(notification)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getUserNotifications(String uid, NotificationListCallback callback) {
        notificationsRef.child(uid).orderByChild("createdAt")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Notification> list = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Notification n = ds.getValue(Notification.class);
                            if (n != null) { n.setId(ds.getKey()); list.add(0, n); } // newest first
                        }
                        callback.onSuccess(list);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void markAsRead(String uid, String notificationId, SimpleCallback callback) {
        notificationsRef.child(uid).child(notificationId).child("isRead").setValue(true)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void markAllRead(String uid) {
        notificationsRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().child("isRead").setValue(true);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void getUnreadCount(String uid, UnreadCountCallback callback) {
        notificationsRef.child(uid).orderByChild("isRead").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onCount((int) snapshot.getChildrenCount());
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        callback.onCount(0);
                    }
                });
    }

    public interface UnreadCountCallback {
        void onCount(int count);
    }
}
