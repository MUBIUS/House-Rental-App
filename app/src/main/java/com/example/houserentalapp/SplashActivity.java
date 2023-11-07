package com.example.houserentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.houserentalapp.screens.HomeActivity;
import com.example.houserentalapp.screens.LoginActivity;
import com.example.houserentalapp.screens.OTPVerificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //splash screen wait for 2 sec and then Launch Login Activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

//                if user is already logged in then it will go to home screen
//                he do not need to login again
//                if(user != null){
//                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
//                    finish();
//                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                    finish();
//                }
//                startActivity(new Intent(SplashActivity.this, OTPVerificationActivity.class));
//                finish();

            }
        },3000);
    }
}