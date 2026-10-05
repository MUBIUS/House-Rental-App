package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Review;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewRepository {

    public interface ReviewListCallback {
        void onSuccess(List<Review> reviews);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference reviewsRef;
    private final DatabaseReference propertiesRef;

    public ReviewRepository() {
        reviewsRef = FirebaseDatabase.getInstance().getReference(Constants.DB_REVIEWS);
        propertiesRef = FirebaseDatabase.getInstance().getReference(Constants.DB_PROPERTIES);
    }

    public void submitReview(Review review, SimpleCallback callback) {
        String id = reviewsRef.child(review.getPropertyId()).push().getKey();
        if (id == null) { callback.onFailure("Failed to generate ID"); return; }
        review.setId(id);
        reviewsRef.child(review.getPropertyId()).child(id).setValue(review)
                .addOnSuccessListener(unused -> {
                    updatePropertyRating(review.getPropertyId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getPropertyReviews(String propertyId, ReviewListCallback callback) {
        reviewsRef.child(propertyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Review> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Review r = ds.getValue(Review.class);
                    if (r != null) { r.setId(ds.getKey()); list.add(r); }
                }
                callback.onSuccess(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void addLandlordResponse(String propertyId, String reviewId,
                                     String response, SimpleCallback callback) {
        reviewsRef.child(propertyId).child(reviewId).child("landlordResponse").setValue(response)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    /** Recalculate and update property's average rating after a new review */
    private void updatePropertyRating(String propertyId) {
        reviewsRef.child(propertyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float total = 0;
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Review r = ds.getValue(Review.class);
                    if (r != null) { total += r.getRating(); count++; }
                }
                if (count > 0) {
                    float avg = total / count;
                    propertiesRef.child(propertyId).child("averageRating").setValue(avg);
                    propertiesRef.child(propertyId).child("reviewCount").setValue(count);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
