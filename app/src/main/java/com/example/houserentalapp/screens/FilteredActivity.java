package com.example.houserentalapp.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.houserentalapp.R;
import com.example.houserentalapp.adapters.HomeAdapter;
import com.example.houserentalapp.model.Item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FilteredActivity extends AppCompatActivity {

    private List<Item> itemList;
    private HomeAdapter adapter;
    private RecyclerView topDealRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered);

        topDealRV = findViewById(R.id.top_deal_RV);


        itemList = new ArrayList<>();

        String filter = getIntent().getStringExtra("Filter");

        if(filter.equals("Home")){

            if (topDealRV != null) {

                FirebaseDatabase.getInstance().getReference().child("Images").child("Home")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

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
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


        } else if (filter.equals("Flat")) {
            if (topDealRV != null) {

                FirebaseDatabase.getInstance().getReference().child("Images").child("Flat")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

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
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

        } else if (filter.equals("Room")) {
            if (topDealRV != null) {

                FirebaseDatabase.getInstance().getReference().child("Images").child("Room")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

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
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }


        adapter = new HomeAdapter(FilteredActivity.this,itemList,this::OnItemPosition);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FilteredActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        topDealRV.setLayoutManager(linearLayoutManager);
        topDealRV.setAdapter(adapter);
    }

    public void OnItemPosition(int position) {

        Intent intent= new Intent(FilteredActivity.this, DetailsActivity.class);
        intent.putExtra("price",itemList.get(position).getPrice());
        intent.putExtra("location",itemList.get(position).getLocation());
        intent.putExtra("description",itemList.get(position).getDescription());
        intent.putExtra("shortDescription",itemList.get(position).getShortDescription());
        intent.putExtra("contactNumber",itemList.get(position).getContactNumber());
        intent.putExtra("image",itemList.get(position).getImage());
        startActivity(intent);

    }
}