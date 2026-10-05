package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Favourite;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRepository {

    public interface FavouriteCheckCallback {
        void onResult(boolean isFavourite);
    }

    public interface FavouriteListCallback {
        void onSuccess(List<String> propertyIds);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference favouritesRef;

    public FavouriteRepository() {
        favouritesRef = FirebaseDatabase.getInstance().getReference(Constants.DB_FAVOURITES);
    }

    public void addFavourite(String uid, String propertyId, SimpleCallback callback) {
        Favourite fav = new Favourite(propertyId);
        favouritesRef.child(uid).child(propertyId).setValue(fav)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void removeFavourite(String uid, String propertyId, SimpleCallback callback) {
        favouritesRef.child(uid).child(propertyId).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void isFavourite(String uid, String propertyId, FavouriteCheckCallback callback) {
        favouritesRef.child(uid).child(propertyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onResult(snapshot.exists());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(false);
                    }
                });
    }

    public void getFavouritePropertyIds(String uid, FavouriteListCallback callback) {
        favouritesRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> ids = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ids.add(ds.getKey());
                }
                callback.onSuccess(ids);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public ValueEventListener observeFavourites(String uid, FavouriteListCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> ids = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) ids.add(ds.getKey());
                callback.onSuccess(ids);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        };
        favouritesRef.child(uid).addValueEventListener(listener);
        return listener;
    }
}
