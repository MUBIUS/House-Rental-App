package com.example.houserentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalapp.model.User;
import com.example.houserentalapp.repository.UserRepository;
import com.example.houserentalapp.screens.admin.AdminHomeActivity;
import com.example.houserentalapp.screens.auth.LoginActivity;
import com.example.houserentalapp.screens.landlord.LandlordHomeActivity;
import com.example.houserentalapp.screens.tenant.TenantHomeActivity;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                Log.d(TAG, "User logged in: " + firebaseUser.getUid());
                SessionManager session = new SessionManager(this);

                // If session UID is missing (e.g., app data cleared but Auth token still valid),
                // re-fetch the user profile from Firebase and save the session before routing.
                if (session.getUid() == null) {
                    Log.d(TAG, "Session missing, fetching profile...");
                    
                    // Safety timeout: if fetching takes too long, move forward with defaults
                    final boolean[] handled = {false};
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (!handled[0]) {
                            handled[0] = true;
                            Log.w(TAG, "Profile fetch timed out, signing out for security.");
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }
                    }, 5000); // 5 second timeout

                    new UserRepository().getOrCreateUser(firebaseUser, new UserRepository.UserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            if (!handled[0]) {
                                handled[0] = true;
                                Log.d(TAG, "Profile fetched successfully");
                                session.saveSession(
                                        user.getUid(),
                                        user.getRole(),
                                        user.getName(),
                                        user.getEmail()
                                );
                                if (user.getProfileImage() != null) {
                                    session.saveProfileImage(user.getProfileImage());
                                }
                                session.saveCurrency(user.getCurrency());
                                routeByRole(user.getRole());
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            if (!handled[0]) {
                                handled[0] = true;
                                Log.e(TAG, "Profile fetch failed: " + errorMessage);
                                // Profile missing in DB (user may be banned/deleted)
                                // Sign out and redirect to login for security.
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Session found, routing by role: " + session.getRole());
                    routeByRole(session.getRole());
                }
            } else {
                Log.d(TAG, "No user logged in, routing to LoginActivity");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }

    private void routeByRole(String role) {
        if (role == null) {
            role = Constants.ROLE_TENANT;
        }
        
        Intent intent;
        switch (role) {
            case Constants.ROLE_LANDLORD:
                intent = new Intent(this, LandlordHomeActivity.class);
                break;
            case Constants.ROLE_ADMIN:
                intent = new Intent(this, AdminHomeActivity.class);
                break;
            default:
                intent = new Intent(this, TenantHomeActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
