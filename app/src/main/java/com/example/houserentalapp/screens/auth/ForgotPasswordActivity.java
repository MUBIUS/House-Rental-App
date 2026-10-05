package com.example.houserentalapp.screens.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.example.houserentalapp.R;
import com.example.houserentalapp.repository.AuthRepository;
import com.example.houserentalapp.utils.ValidationUtils;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private AppCompatButton btnReset;
    private ProgressBar progressBar;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authRepository = new AuthRepository();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reset Password");
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        etEmail = findViewById(R.id.user_email);
        btnReset = findViewById(R.id.btn_reset);
        progressBar = findViewById(R.id.progress_bar);

        btnReset.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!ValidationUtils.isValidEmail(email)) {
                etEmail.setError("Enter a valid email address");
                etEmail.requestFocus();
                return;
            }
            setLoading(true);
            authRepository.sendPasswordReset(email, task -> {
                setLoading(false);
                if (task.isSuccessful()) {
                    Toast.makeText(this,
                            "Reset link sent to " + email + ". Check your inbox.",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this,
                            task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Failed to send reset email",
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnReset.setEnabled(!loading);
        btnReset.setText(loading ? "Sending..." : "Send Reset Link");
    }
}
