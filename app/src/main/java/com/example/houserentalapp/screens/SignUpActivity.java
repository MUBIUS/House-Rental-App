package com.example.houserentalapp.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.houserentalapp.R;
import com.example.houserentalapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextView haveAccount;
    private FirebaseAuth mAuth;
    private EditText userName, userEmail, userPassword, confirmPassword;
    private AppCompatButton signUpButton;
    private DatabaseReference mRef;
    private EditText mobile;
    private ImageView passwordIcon;
    private ImageView confirmPasswordIcon;
    private boolean passwordShowing = false;
    private boolean confirmPasswordShowing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        haveAccount = findViewById(R.id.have_account);
        userName =findViewById(R.id.user_name);
        userEmail =findViewById(R.id.user_email);
        userPassword =findViewById(R.id.user_password);
        passwordIcon =findViewById(R.id.passwordIcon);
        confirmPassword =findViewById(R.id.confirm_password);
        confirmPasswordIcon =findViewById(R.id.confirm_passwordIcon);
        signUpButton =findViewById(R.id.sign_up_button);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();


        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                finish();
            }
        });

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //checking if password is showing or not
                if(passwordShowing){
                    passwordShowing = false;

                    userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_show);
                }else{
                    passwordShowing = true;

                    userPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_hide);
                }
            }
        });

        confirmPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //checking if password is showing or not
                if(confirmPasswordShowing){
                    confirmPasswordShowing = false;

                    confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirmPasswordIcon.setImageResource(R.drawable.password_show);
                }else{
                    confirmPasswordShowing = true;

                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPasswordIcon.setImageResource(R.drawable.password_hide);
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                final String getMobileTxt = mobile.getText().toString();
//                final String getEmailTxt = userEmail.getText().toString();

                //opening OTP Verification Activity along wih mobile and email
//                Intent intent= new Intent(SignUpActivity.this,OTPVerificationActivity.class);
//                intent.putExtra("mobile",getMobileTxt);
//                intent.putExtra("email",getEmailTxt);
//                startActivity(intent);

                if(userName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter Name", Toast.LENGTH_SHORT).show();
                } else if (userEmail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter valid Email", Toast.LENGTH_SHORT).show();
                } else if (userPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                } else if (!userPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
                    Toast.makeText(SignUpActivity.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                } else {
                    if(emailChecker(userEmail.getText().toString().trim())){
                        createUser(userEmail.getText().toString().trim(),
                                userPassword.getText().toString().trim(),
                                userName.getText().toString().trim());
                    }else {

                        Toast.makeText(SignUpActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });


    }

    boolean emailChecker(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    void createUser (String email, String password, String name){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                User user =new User(name,email);

                if(task.isSuccessful()){ 

                    //save data in Firebase database with automatic generated key
                    //push() function for use => automatic key generation
                    mRef.child("Users").push().setValue(user);
                    startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
                    Toast.makeText(SignUpActivity.this, "User Created Successfully..", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(SignUpActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }


}