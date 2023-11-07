package com.example.houserentalapp.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.houserentalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddActivity extends AppCompatActivity {
    private int Pick_Image = 1;
    private AppCompatButton upload_Image;
    private ImageView property_Image;
    private Uri uri;
    private AppCompatButton submit;
    private DatabaseReference reference ;
    private EditText price;
    private EditText shortDescription;
    private EditText description;
    private EditText location;
    private EditText contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home);

        upload_Image = findViewById(R.id.upload_image);
        property_Image = findViewById(R.id.property_image);
        price = findViewById(R.id.price);
        shortDescription = findViewById(R.id.short_description);
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);
        contactInfo = findViewById(R.id.contact_info);

        submit = findViewById(R.id.add_btn);

        upload_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               selectImage();


            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               uploadToDatabase();
               finish();

            }
        });


    }

    private void selectImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selected Picture"),Pick_Image);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Pick_Image && resultCode == RESULT_OK) {

            uri = data.getData();
            property_Image.setImageURI(uri);
        }

    }

    private void uploadToDatabase() {
        // Upload image to Firebase Storage
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference().child("images/" + UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("URL", "onSuccess: " + uri);

                        // Update data in Firebase Database
                        Map<String, String> map = new HashMap<>();
                        map.put("price", price.getText().toString());
                        map.put("shortDescription", shortDescription.getText().toString());
                        map.put("description", description.getText().toString());
                        map.put("location", location.getText().toString());
                        map.put("image", uri.toString());
                        map.put("number",contactInfo.getText().toString());
                        String home = getIntent().getStringExtra("Add");

                        switch (home) {
                            case "Home":
                                FirebaseDatabase.getInstance().getReference().child("Images").child("Home")
                                        .push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddActivity.this, "", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                break;
                            case "Flat":
                                FirebaseDatabase.getInstance().getReference().child("Images").child("Flat")
                                        .push().setValue(map);
                                break;
                            case "Room":
                                FirebaseDatabase.getInstance().getReference().child("Images").child("Room")
                                        .push().setValue(map);
                                break;
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddActivity.this, "Failed" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}




