package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.houserentalapp.model.User;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public interface UserListCallback {
        void onSuccess(List<User> users);
        void onFailure(String errorMessage);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    private final DatabaseReference usersRef;

    public UserRepository() {
        usersRef = FirebaseDatabase.getInstance().getReference(Constants.DB_USERS);
    }

    public void getUser(String uid, UserCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return;
        }
        usersRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.setUid(snapshot.getKey());
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("User not found");
                }
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    /**
     * Fetches user profile or automatically creates a fallback profile if missing in DB.
     */
    public void getOrCreateUser(FirebaseUser firebaseUser, UserCallback callback) {
        if (firebaseUser == null || firebaseUser.getUid() == null) {
            callback.onFailure("User is null");
            return;
        }
        String uid = firebaseUser.getUid();
        usersRef.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.setUid(snapshot.getKey());
                    callback.onSuccess(user);
                } else {
                    // Profile missing in DB — create fallback profile
                    User fallbackUser = createFallbackUser(firebaseUser);
                    usersRef.child(uid).setValue(fallbackUser).addOnCompleteListener(saveTask -> {
                        if (saveTask.isSuccessful()) {
                            callback.onSuccess(fallbackUser);
                        } else {
                            String err = saveTask.getException() != null ? saveTask.getException().getMessage() : "Failed to create user profile";
                            callback.onFailure("Profile missing in database and auto-creation failed: " + err);
                        }
                    });
                }
            } else {
                String err = task.getException() != null ? task.getException().getMessage() : "Unknown database error";
                callback.onFailure(err);
            }
        });
    }

    private User createFallbackUser(FirebaseUser firebaseUser) {
        String email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "";
        String name = firebaseUser.getDisplayName();
        if (name == null || name.trim().isEmpty()) {
            if (email.contains("@")) {
                name = email.substring(0, email.indexOf("@"));
            } else {
                name = "User";
            }
        }
        String phone = firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "";
        return new User(firebaseUser.getUid(), name, email, phone, Constants.ROLE_TENANT);
    }

    /**
     * Observe a user profile in real-time.
     * IMPORTANT: The caller must store the returned listener and call
     * usersRef.child(uid).removeEventListener(listener) when done (e.g., in onDestroyView).
     */
    public ValueEventListener observeUser(String uid, UserCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return null;
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    user.setUid(snapshot.getKey());
                    callback.onSuccess(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        };
        usersRef.child(uid).addValueEventListener(listener);
        return listener;
    }

    public void removeUserObserver(String uid, ValueEventListener listener) {
        if (listener != null) usersRef.child(uid).removeEventListener(listener);
    }

    public void updateUser(String uid, User user, SimpleCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return;
        }
        user.setUpdatedAt(System.currentTimeMillis());
        usersRef.child(uid).setValue(user)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateField(String uid, String field, Object value, SimpleCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return;
        }
        usersRef.child(uid).child(field).setValue(value)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getAllUsers(UserListCallback callback) {
        usersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                List<User> users = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null) {
                        user.setUid(ds.getKey());
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    public void getUsersByRole(String role, UserListCallback callback) {
        usersRef.orderByChild("role").equalTo(role).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                List<User> users = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null) {
                        user.setUid(ds.getKey());
                        users.add(user);
                    }
                }
                callback.onSuccess(users);
            } else {
                callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Unknown error");
            }
        });
    }

    public void setVerified(String uid, boolean verified, SimpleCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return;
        }
        usersRef.child(uid).child("verified").setValue(verified)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void setActive(String uid, boolean active, SimpleCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return;
        }
        usersRef.child(uid).child("active").setValue(active)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void deleteUser(String uid, SimpleCallback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onFailure("User ID is null or empty");
            return;
        }
        usersRef.child(uid).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}
