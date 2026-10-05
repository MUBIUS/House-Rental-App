package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.User;
import com.example.houserentalapp.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AuthRepository {

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    private final FirebaseAuth auth;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(auth.getCurrentUser());
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Login failed. Please try again.";
                        callback.onFailure(msg);
                    }
                });
    }

    public void register(String email, String password, User userProfile,
                         AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Store user profile under /users/{uid}
                            userProfile.setUid(firebaseUser.getUid());
                            FirebaseDatabase.getInstance()
                                    .getReference(Constants.DB_USERS)
                                    .child(firebaseUser.getUid())
                                    .setValue(userProfile)
                                    .addOnSuccessListener(unused -> {
                                        // Send email verification
                                        firebaseUser.sendEmailVerification()
                                                .addOnCompleteListener(verifyTask -> {
                                                    // Proceed regardless of verification send status
                                                    callback.onSuccess(firebaseUser);
                                                });
                                    })
                                    .addOnFailureListener(e ->
                                            callback.onFailure(e.getMessage()));
                        }
                    } else {
                        String msg = task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed. Please try again.";
                        callback.onFailure(msg);
                    }
                });
    }

    public void sendPasswordReset(String email, OnCompleteListener<Void> listener) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(listener);
    }

    public void signOut() {
        auth.signOut();
    }

    public void deleteAccount(OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(listener);
        }
    }
}
