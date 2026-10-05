package com.example.houserentalapp.fragments.tenant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.User;
import com.example.houserentalapp.repository.AuthRepository;
import com.example.houserentalapp.repository.UserRepository;
import com.example.houserentalapp.screens.auth.LoginActivity;
import com.example.houserentalapp.utils.Constants;
import com.example.houserentalapp.utils.CurrencyUtils;
import com.example.houserentalapp.utils.ImageUtils;
import com.example.houserentalapp.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class ProfileFragment extends Fragment {

    private ImageView ivProfileImage;
    private FloatingActionButton fabEditImage;
    private TextView tvDisplayName, tvDisplayEmail;
    private EditText etName, etPhone;
    private Button btnSaveProfile, btnLogout;
    private SwitchMaterial switchDarkMode;
    private AutoCompleteTextView autoCompleteCurrency;

    private SessionManager session;
    private UserRepository userRepository;
    private AuthRepository authRepository;
    private User currentUser;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    launchImagePicker();
                } else {
                    Toast.makeText(requireContext(),
                            "Storage permission is required to change profile picture",
                            Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProfileImage.setImageURI(selectedImageUri);
                    uploadProfileImage();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        session = new SessionManager(requireContext());
        userRepository = new UserRepository();
        authRepository = new AuthRepository();

        bindViews(view);
        setupCurrencyDropdown();
        loadLocalData();
        fetchUserData();
        setListeners();

        return view;
    }

    private void bindViews(View view) {
        ivProfileImage = view.findViewById(R.id.iv_profile_image);
        fabEditImage = view.findViewById(R.id.fab_edit_image);
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvDisplayEmail = view.findViewById(R.id.tv_display_email);
        etName = view.findViewById(R.id.et_name);
        etPhone = view.findViewById(R.id.et_phone);
        btnSaveProfile = view.findViewById(R.id.btn_save_profile);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        autoCompleteCurrency = view.findViewById(R.id.auto_complete_currency);
        btnLogout = view.findViewById(R.id.btn_logout);
    }

    private void setupCurrencyDropdown() {
        String[] displayNames = CurrencyUtils.getCurrencyDisplayNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, displayNames);
        autoCompleteCurrency.setAdapter(adapter);
    }

    private void loadLocalData() {
        tvDisplayName.setText(session.getName());
        tvDisplayEmail.setText(session.getEmail());
        etName.setText(session.getName());
        
        switchDarkMode.setChecked(session.isDarkMode());

        String[] currencies = CurrencyUtils.getSupportedCurrencies();
        int index = Arrays.asList(currencies).indexOf(session.getCurrency());
        if (index >= 0) {
            autoCompleteCurrency.setText(CurrencyUtils.getCurrencyDisplayNames()[index], false);
        }

        if (session.getProfileImage() != null) {
            ImageUtils.loadAvatar(requireContext(), session.getProfileImage(), ivProfileImage);
        }
    }

    private void fetchUserData() {
        String uid = session.getUid();
        if (uid == null || uid.isEmpty()) return;

        userRepository.getUser(uid, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                if (!isAdded()) return;
                currentUser = user;
                etName.setText(user.getName());
                etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                tvDisplayName.setText(user.getName());
                tvDisplayEmail.setText(user.getEmail());
                
                if (user.getProfileImage() != null) {
                    session.saveProfileImage(user.getProfileImage());
                    ImageUtils.loadAvatar(requireContext(), user.getProfileImage(), ivProfileImage);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to load profile details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setListeners() {
        fabEditImage.setOnClickListener(v -> checkPermissionAndPickImage());

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            session.setDarkMode(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        btnLogout.setOnClickListener(v -> {
            authRepository.signOut();
            session.clear();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finishAffinity();
        });
    }

    private void saveProfile() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

        String selectedDisplayName = autoCompleteCurrency.getText().toString();
        String[] displayNames = CurrencyUtils.getCurrencyDisplayNames();
        String[] currencies = CurrencyUtils.getSupportedCurrencies();
        
        String currency = session.getCurrency();
        for (int i = 0; i < displayNames.length; i++) {
            if (displayNames[i].equals(selectedDisplayName)) {
                currency = currencies[i];
                break;
            }
        }

        if (name.isEmpty()) {
            etName.setError("Name cannot be empty");
            return;
        }

        if (currentUser == null) {
            currentUser = new User();
            currentUser.setUid(session.getUid());
            currentUser.setEmail(session.getEmail() != null ? session.getEmail() : "");
            currentUser.setRole(session.getRole());
        }

        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setCurrency(currency);

        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Saving...");

        final String finalCurrency = currency;
        userRepository.updateUser(session.getUid(), currentUser, new UserRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                String updatedEmail = currentUser.getEmail() != null ? currentUser.getEmail() : session.getEmail();
                session.saveSession(session.getUid(), currentUser.getRole(), name, updatedEmail);
                session.saveCurrency(finalCurrency);
                
                tvDisplayName.setText(name);
                tvDisplayEmail.setText(updatedEmail);

                btnSaveProfile.setEnabled(true);
                btnSaveProfile.setText("Save Changes");
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (isAdded()) {
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Save Changes");
                    Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadProfileImage() {
        if (selectedImageUri == null) return;

        String uid = session.getUid();
        if (uid == null || uid.isEmpty()) return;

        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child(Constants.STORAGE_PROFILE_IMAGES + uid + ".jpg");

        Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show();

        ref.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    userRepository.updateField(session.getUid(), "profileImage", downloadUrl, new UserRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            if (!isAdded()) return;
                            session.saveProfileImage(downloadUrl);
                            Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }))
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkPermissionAndPickImage() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        } else {
            permissionLauncher.launch(permission);
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
