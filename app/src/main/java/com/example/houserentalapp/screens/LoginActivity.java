package com.example.houserentalapp.screens;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.appcompat.app.ActionBar;

import com.example.houserentalapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private RelativeLayout googleBtn;
    private TextView createAccount;

    private EditText email,password;
    private AppCompatButton loginButton;
    private FirebaseAuth mAuth;
    private ImageView passwordIcon;
    private boolean passwordShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        createAccount = findViewById(R.id.dont_have_account);
        email =findViewById(R.id.user_email);
        password =findViewById(R.id.user_password);
        loginButton =findViewById(R.id.login_button);
        passwordIcon =findViewById(R.id.passwordIcon);


        googleBtn = findViewById(R.id.signInWithGoogle);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        mAuth = FirebaseAuth.getInstance();

        passwordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Checking if password is showing or not
                if (passwordShowing){
                    passwordShowing = false;

                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_show);
                }else {
                    passwordShowing = true;

                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordIcon.setImageResource(R.drawable.password_hide);
                }

                //move the cursor at last of the text
                password.setSelection(password.length());
            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

            googleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!email.getText().toString().trim().isEmpty() && emailChecker(email.getText().toString().trim())) {

                        if (!password.getText().toString().isEmpty()) {
                            loginUser(email.getText().toString().trim(), password.getText().toString().trim());
                        } else {
                            Toast.makeText(LoginActivity.this, "Enter Valid Password", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }



    private void signIn() {

        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000){

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                task.getResult(ApiException.class);
                navigateUpToNextActivity();

            } catch (ApiException e) {

                Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void navigateUpToNextActivity() {

        finish();
        Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
        startActivity(intent);

    }

    boolean emailChecker(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    void loginUser(String email ,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();

                }else {
                    Toast.makeText(LoginActivity.this, "Fail...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Fail...", Toast.LENGTH_SHORT).show();
            }
        });
    }


}