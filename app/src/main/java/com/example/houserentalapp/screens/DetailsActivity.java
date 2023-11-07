package com.example.houserentalapp.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import android.Manifest;
import com.example.houserentalapp.R;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView price;
    private TextView shortDescription;
    private TextView description;
    private TextView contactNumber;
    String pri,shdes,des,img,number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        imageView = findViewById(R.id.imageView);
        price = findViewById(R.id.price);
        shortDescription = findViewById(R.id.short_description);
        description = findViewById(R.id.description);
        contactNumber = findViewById(R.id.contact_info);

        pri = getIntent().getStringExtra("price");
        des = getIntent().getStringExtra("description");
        shdes = getIntent().getStringExtra("shortDescription");
        img = getIntent().getStringExtra("image");
        number = getIntent().getStringExtra("contactNumber");

        price.setText(pri);
        description.setText(des);
        shortDescription.setText(shdes);
        contactNumber.setText(number);
        Glide.with(this)
                .load(img)
                .centerCrop()
                .placeholder(R.drawable.ic_account)
                .into(imageView);

        contactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });


    }
    public void makePhoneCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            try {
                String phoneNumber ="tel:"+contactNumber.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(phoneNumber));
                startActivity(intent);
            }catch (Exception e){
                Toast.makeText(this, "Exception:"+e, Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}