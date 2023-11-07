package com.example.houserentalapp.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.houserentalapp.R;
import com.example.houserentalapp.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class    AccountFragment extends Fragment {

    private CircleImageView userProfile;
    private EditText userName, userEmail;
    private AppCompatButton updateButton;
    private AppCompatButton deleteButton;
    private DatabaseReference ref;
    private int Pick_Image = 1;
    private GoogleSignInAccount account;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;
    private Uri uri;
    private String id;
    private DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userProfile = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        updateButton = view.findViewById(R.id.update_button);

        deleteButton = view.findViewById(R.id.delete_account);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getContext(),googleSignInOptions);


        account = GoogleSignIn.getLastSignedInAccount(getContext());

        if (account != null) {

            userEmail.setText(account.getEmail());
            userProfile.setImageURI(account.getPhotoUrl());
            userName.setText(account.getDisplayName());

        }

        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ref.addChildEventListener(new ChildEventListener()  {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                User user = snapshot.getValue(User.class);
                if(user != null){
                    userName.setText(user.getname());  // set text to the value of user's name field
                    userEmail.setText(user.getEmail()); // set text to the value of user's email field
                    id = snapshot.getKey();
                    Glide
                            .with(getContext())
                            .load(user.getImage())
                            .centerCrop()
                            .placeholder(R.drawable.ic_account)
                            .into(userProfile);
                }
                else {

                    userProfile.setImageResource(R.drawable.ic_account);
                    userName.setHint("User Name");
                    userEmail.setHint("Email");

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Selected Picture"),Pick_Image);

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    uploadImage();
                }catch (Exception e){
                    Toast.makeText(getContext(), "Execption:"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                assert user != null;
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                }
                            }
                        });
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Pick_Image && resultCode == RESULT_OK) {
            assert data != null;
            if (data.getData() != null) {

                uri = data.getData();
                userProfile.setImageURI(uri);

            }
            else {
                userProfile.setImageResource(R.drawable.ic_account);
            }
        }

    }

    private void uploadImage() {

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference().child("images/"+ UUID.randomUUID().toString());
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Log.d("URL","onSuccess: "+uri);

                        //update data in Firebase Database
                        Map<String,String> map = new HashMap<>();
                        map.put("name",userName.getText().toString());
                        map.put("email",userEmail.getText().toString());
                        if(uri != null){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                map.putIfAbsent("image",uri.toString());
                            }
                        }
                        else{
                            Toast.makeText(getContext(), "Select a Image", Toast.LENGTH_SHORT).show();

                        }

                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
                        reference.setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                //update user authenticated email
                                FirebaseAuth.getInstance().getCurrentUser()
                                        .updateEmail(userEmail.getText().toString().trim())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isComplete()){
                                                    Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

                                                }else {
                                                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }
                        });

                    }

                });
            }
        });
    }
}