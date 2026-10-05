package com.example.houserentalapp.screens.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.User;
import com.example.houserentalapp.repository.AuthRepository;
import com.example.houserentalapp.repository.UserRepository;
import com.example.houserentalapp.screens.admin.AdminHomeActivity;
import com.example.houserentalapp.screens.landlord.LandlordHomeActivity;
import com.example.houserentalapp.screens.tenant.TenantHomeActivity;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.SessionManager;
import com.example.houserentalapp.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivPasswordToggle;
    private AppCompatButton btnLogin;
    private TextView tvCreateAccount, tvForgotPassword;
    private ProgressBar progressBar;
    private boolean passwordVisible = false;

    private AuthRepository authRepository;
    private UserRepository userRepository;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();
        userRepository = new UserRepository();
        session = new SessionManager(this);

        bindViews();
        setListeners();
    }

    private void bindViews() {
        etEmail = findViewById(R.id.user_email);
        etPassword = findViewById(R.id.user_password);
        ivPasswordToggle = findViewById(R.id.passwordIcon);
        btnLogin = findViewById(R.id.login_button);
        tvCreateAccount = findViewById(R.id.dont_have_account);
        tvForgotPassword = findViewById(R.id.forgot_password);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setListeners() {
        ivPasswordToggle.setOnClickListener(v -> togglePasswordVisibility());

        tvCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void togglePasswordVisibility() {
        if (passwordVisible) {
            passwordVisible = false;
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.password_show);
        } else {
            passwordVisible = true;
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivPasswordToggle.setImageResource(R.drawable.password_hide);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString(); // Do NOT trim passwords

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        if (!ValidationUtils.isNotEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        setLoading(true);
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                // Fetch full user profile to get role
                userRepository.getUser(user.getUid(), new UserRepository.UserCallback() {
                    @Override
                    public void onSuccess(User userProfile) {
                        setLoading(false);
                        session.saveSession(
                                userProfile.getUid(),
                                userProfile.getRole(),
                                userProfile.getName(),
                                userProfile.getEmail()
                        );
                        if (userProfile.getProfileImage() != null) {
                            session.saveProfileImage(userProfile.getProfileImage());
                        }
                        session.saveCurrency(userProfile.getCurrency());
                        routeByRole(userProfile.getRole());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        setLoading(false);
                        // Profile fetch failed — do NOT silently grant access.
                        // Sign out and ask the user to try again.
                        authRepository.signOut();
                        Toast.makeText(LoginActivity.this,
                                "Could not load your profile. Please check your connection and try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void routeByRole(String role) {
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

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? "Logging in..." : "Login");
    }
}
