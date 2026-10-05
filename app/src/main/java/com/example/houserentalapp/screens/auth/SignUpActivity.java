package com.example.houserentalapp.screens.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.User;
import com.example.houserentalapp.repository.AuthRepository;
import com.example.houserentalapp.screens.landlord.LandlordHomeActivity;
import com.example.houserentalapp.screens.tenant.TenantHomeActivity;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.SessionManager;
import com.example.houserentalapp.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private ImageView ivPasswordToggle, ivConfirmToggle;
    private RadioGroup rgRole;
    private RadioButton rbTenant, rbLandlord;
    private Spinner spinnerCurrency;
    private AppCompatButton btnSignUp;
    private TextView tvHaveAccount;
    private ProgressBar progressBar;

    private boolean passwordVisible = false;
    private boolean confirmVisible = false;

    private AuthRepository authRepository;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        authRepository = new AuthRepository();
        session = new SessionManager(this);

        bindViews();
        setupCurrencySpinner();
        setListeners();
    }

    private void bindViews() {
        etName = findViewById(R.id.user_name);
        etEmail = findViewById(R.id.user_email);
        etPhone = findViewById(R.id.user_phone);
        etPassword = findViewById(R.id.user_password);
        etConfirmPassword = findViewById(R.id.confirm_password);
        ivPasswordToggle = findViewById(R.id.passwordIcon);
        ivConfirmToggle = findViewById(R.id.confirm_passwordIcon);
        rgRole = findViewById(R.id.rg_role);
        rbTenant = findViewById(R.id.rb_tenant);
        rbLandlord = findViewById(R.id.rb_landlord);
        spinnerCurrency = findViewById(R.id.spinner_currency);
        btnSignUp = findViewById(R.id.sign_up_button);
        tvHaveAccount = findViewById(R.id.have_account);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupCurrencySpinner() {
        String[] displayNames = CurrencyUtils.getCurrencyDisplayNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, displayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
    }

    private void setListeners() {
        tvHaveAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        ivPasswordToggle.setOnClickListener(v -> {
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
        });

        ivConfirmToggle.setOnClickListener(v -> {
            if (confirmVisible) {
                confirmVisible = false;
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivConfirmToggle.setImageResource(R.drawable.password_show);
            } else {
                confirmVisible = true;
                etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivConfirmToggle.setImageResource(R.drawable.password_hide);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });

        btnSignUp.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString(); // Do NOT trim passwords
        String confirm = etConfirmPassword.getText().toString(); // Do NOT trim passwords

        if (!ValidationUtils.isNotEmpty(name)) {
            etName.setError("Name is required"); etName.requestFocus(); return;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Enter a valid email"); etEmail.requestFocus(); return;
        }
        if (phone.length() > 0 && !ValidationUtils.isValidPhone(phone)) {
            etPhone.setError("Enter a valid phone number"); etPhone.requestFocus(); return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 8 characters with letters and numbers");
            etPassword.requestFocus(); return;
        }
        if (!ValidationUtils.passwordsMatch(password, confirm)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus(); return;
        }

        String role = rbLandlord.isChecked() ? Constants.ROLE_LANDLORD : Constants.ROLE_TENANT;
        String[] currencies = CurrencyUtils.getSupportedCurrencies();
        String currency = currencies[spinnerCurrency.getSelectedItemPosition()];

        User userProfile = new User();
        userProfile.setName(name);
        userProfile.setEmail(email);
        userProfile.setPhone(phone);
        userProfile.setRole(role);
        userProfile.setCurrency(currency);

        setLoading(true);
        authRepository.register(email, password, userProfile, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(FirebaseUser user) {
                setLoading(false);
                session.saveSession(user.getUid(), role, name, email);
                session.saveCurrency(currency);
                Toast.makeText(SignUpActivity.this,
                        "Account created! A verification email has been sent to " + email,
                        Toast.LENGTH_LONG).show();

                Intent intent = Constants.ROLE_LANDLORD.equals(role)
                        ? new Intent(SignUpActivity.this, LandlordHomeActivity.class)
                        : new Intent(SignUpActivity.this, TenantHomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                setLoading(false);
                Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!loading);
        btnSignUp.setText(loading ? "Creating account..." : "Sign Up");
    }
}
