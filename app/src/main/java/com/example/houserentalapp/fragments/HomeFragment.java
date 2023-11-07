package com.example.houserentalapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.houserentalapp.R;
import com.example.houserentalapp.adapters.HomeAdapter;
import com.example.houserentalapp.listeners.ItemListener;
import com.example.houserentalapp.model.Item;
import com.example.houserentalapp.model.User;
import com.example.houserentalapp.screens.DetailsActivity;
import com.example.houserentalapp.screens.FilteredActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment implements ItemListener {

    private RecyclerView topDealRV;
    private RecyclerView homeDealRV;
    private RecyclerView flatDealRV;
    private RecyclerView roomDealRV;
    private HomeAdapter adapter;
    private HomeAdapter adapterHome;
    private HomeAdapter adapterFlat;
    private HomeAdapter adapterRoom;
    private List<Item> itemList, homeList, flatList, roomList, topList;
    private CircleImageView profileImage;
    private TextView username;
    private DatabaseReference ref;
    private RelativeLayout homeBtn;
    private RelativeLayout flatBtn;
    private RelativeLayout roomBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        topDealRV = view.findViewById(R.id.top_deal_RV);
        homeDealRV = view.findViewById(R.id.home_deal_RV);
        flatDealRV = view.findViewById(R.id.flat_deal_RV);
        roomDealRV = view.findViewById(R.id.room_deal_RV);

        profileImage = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.user_name);

        homeBtn = view.findViewById(R.id.homeBtn);
        flatBtn = view.findViewById(R.id.flatBtn);
        roomBtn = view.findViewById(R.id.roomBtn);


        try {

            ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.addChildEventListener(new ChildEventListener()  {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        User user = snapshot.getValue(User.class);
                        if(user != null){
                            username.setText(user.getname());  // set text to the value of user's name field
                            Glide
                                    .with(getContext())
                                    .load(user.getImage())
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_account)
                                    .into(profileImage);
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

        }catch (Exception e){
            Toast.makeText(getContext(), "Exception:"+e, Toast.LENGTH_SHORT).show();
        }


        itemList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Images").child("Home")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            itemList.add(new Item(
                                    Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()
                            ));

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Images").child("Flat")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            itemList.add(new Item(
                                    Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()
                            ));

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Images").child("Room")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            itemList.add(new Item(
                                    Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()
                            ));

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){

                    }
                });

        try {
            adapter = new HomeAdapter(getContext(),itemList, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            topDealRV.setLayoutManager(linearLayoutManager);
            topDealRV.setAdapter(adapter);
        }catch (Exception e){
            Toast.makeText(getContext(), "Exception:"+e, Toast.LENGTH_SHORT).show();
        }



        homeList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Images").child("Home")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            homeList.add(new Item(
                                    Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()
                            ));

                        }
                        adapterHome.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){

                    }
                });

        try {
            adapterHome = new HomeAdapter(getContext(),homeList, this);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext());
            linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
            homeDealRV.setLayoutManager(linearLayoutManager1);
            homeDealRV.setAdapter(adapterHome);
        }catch (Exception e){
            Toast.makeText(getContext(), "Exception:"+e, Toast.LENGTH_SHORT).show();
        }


        flatList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Images").child("Flat")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            flatList.add(new Item(
                                    Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()
                            ));

                        }
                        adapterFlat.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){

                    }
                });

        try {
            adapterFlat = new HomeAdapter(getContext(),flatList, this);
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
            linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
            flatDealRV.setLayoutManager(linearLayoutManager2);
            flatDealRV.setAdapter(adapterFlat);
        }catch (Exception e){
            Toast.makeText(getContext(), "Exception:"+e, Toast.LENGTH_SHORT).show();
        }


        roomList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Images").child("Room")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot){

                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                            roomList.add(new Item(
                                    Objects.requireNonNull(dataSnapshot.child("location").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("shortDescription").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("number").getValue()).toString(),
                                    Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()
                            ));

                        }
                        adapterRoom.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error){

                    }
                });

        try {
            adapterRoom = new HomeAdapter(getContext(),roomList, this);
            LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext());
            linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
            roomDealRV.setLayoutManager(linearLayoutManager3);
            roomDealRV.setAdapter(adapterRoom);
        }catch (Exception e){
            Toast.makeText(getContext(), "Exception:"+e, Toast.LENGTH_SHORT).show();
        }



        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),FilteredActivity.class);
                intent.putExtra("Filter","Home");
                startActivity(intent);

            }
        });

        flatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),FilteredActivity.class);
                intent.putExtra("Filter","Flat");
                startActivity(intent);

            }
        });

        roomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),FilteredActivity.class);
                intent.putExtra("Filter","Room");
                startActivity(intent);

            }
        });

    }

    @Override
    public void OnItemPosition(int position) {

        try {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                intent.putExtra("price", itemList.get(position).getPrice());
                intent.putExtra("location", itemList.get(position).getLocation());
                intent.putExtra("description", itemList.get(position).getDescription());
                intent.putExtra("shortDescription", itemList.get(position).getShortDescription());
                intent.putExtra("contactNumber", itemList.get(position).getContactNumber());
                intent.putExtra("image", itemList.get(position).getImage());
                startActivity(intent);
        }catch (Exception e){
            Toast.makeText(getContext(), "Exception:"+e, Toast.LENGTH_SHORT).show();
        }


    }



}