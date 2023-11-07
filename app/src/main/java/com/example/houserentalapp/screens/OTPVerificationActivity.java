package com.example.houserentalapp.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.houserentalapp.R;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText otp1,otp2,otp3,otp4,otp5,otp6;
    private TextView resend_button;
    private TextView otpEmail,otpMobile;
    private Button verify;

    //true after every 60 seconds
    private boolean resendEnable = false;

    //resend time in seconds
    private int resendTime = 60;

    private int selectedETPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
//        otp5 = findViewById(R.id.otp5);
//        otp6 = findViewById(R.id.otp6);

        resend_button = findViewById(R.id.resendOtp);

        verify = findViewById(R.id.verify_button);

        otpEmail = findViewById(R.id.otpEmail);
        otpMobile = findViewById(R.id.otpMobile);

        //getting email and mobile from SignUpActivity through intent
        final String getEmail = getIntent().getStringExtra("email");
        final String getMobile = getIntent().getStringExtra("mobile");

        //setting mobile and email
        otpEmail.setText(getEmail);
        otpMobile.setText(getMobile);

        otp1.addTextChangedListener(textWatcher);
        otp2.addTextChangedListener(textWatcher);
        otp3.addTextChangedListener(textWatcher);
        otp4.addTextChangedListener(textWatcher);

        //by default open keyboard at otp1
        showKeyboard(otp1);

        //start resend count down timer
        startCountDownTimer();

        resend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resendEnable){

                    //start new resend count down timer
                    startCountDownTimer();
                }
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String generateOtp = otp1.getText().toString()+otp2.getText().toString()+otp3.getText().toString()+otp4.getText().toString();

                if (generateOtp.length() == 4){

                }
            }
        });


    }

    private void showKeyboard(EditText otpEt){

        otpEt.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpEt, inputMethodManager.SHOW_IMPLICIT);

    }

    private void startCountDownTimer(){

        resendEnable = false;
        resend_button.setTextColor(Color.parseColor("#99000000"));

        new CountDownTimer(resendTime * 1000, 1000){

            @Override
            public void onTick(long l) {
                resend_button.setText("Resend Code ("+(l / 1000)+")");
            }

            @Override
            public void onFinish() {

                resendEnable = true;
                resend_button.setText("Resend Code");
                resend_button.setTextColor(getResources().getColor(R.color.primary));
            }
        }.start();

    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (editable.length() > 0){

                if (selectedETPosition == 0){

                    selectedETPosition = 1;
                    showKeyboard(otp2);

                }else if (selectedETPosition == 1){

                    selectedETPosition = 2;
                    showKeyboard(otp3);

                } else if (selectedETPosition == 2) {

                    selectedETPosition = 3;
                    showKeyboard(otp4);
                }
            }
        }
    };

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_DEL){

            if(selectedETPosition == 3){

                selectedETPosition = 2;
                showKeyboard(otp3);
            } else if (selectedETPosition == 2) {

                selectedETPosition = 1;
                showKeyboard(otp2);
            } else if (selectedETPosition == 1) {

                selectedETPosition = 0;
                showKeyboard(otp1);
            }

            return true;
        }
        else{
            return super.onKeyUp(keyCode, event);
        }
    }
}