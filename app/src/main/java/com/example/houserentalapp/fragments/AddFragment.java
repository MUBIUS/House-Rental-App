package com.example.houserentalapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.houserentalapp.R;
import com.example.houserentalapp.screens.AddActivity;

public class AddFragment extends Fragment {

    private RelativeLayout homebtn, flatbtn, roombtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homebtn = view.findViewById(R.id.homeBtn);
        flatbtn = view.findViewById(R.id.flatBtn);
        roombtn = view.findViewById(R.id.roomBtn);

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(getContext(), AddActivity.class);
                intent.putExtra("Add","Home");
                startActivity(intent);
            }
        });

        flatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), AddActivity.class);
                intent.putExtra("Add","Flat");
                startActivity(intent);
            }
        });

        roombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), AddActivity.class);
                intent.putExtra("Add","Room");
                startActivity(intent);

            }
        });
    }
}