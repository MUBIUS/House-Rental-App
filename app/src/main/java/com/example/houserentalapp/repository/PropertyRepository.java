package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.houserentalapp.model.Property;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class PropertyRepository {

    public interface PropertyListCallback {
        void onSuccess(List<Property> properties);
        void onFailure(String errorMessage);
    }

    public interface PropertyCallback {
        void onSuccess(Property property);
        void onFailure(String errorMessage);
    }

    private final DatabaseReference propertiesRef;

    public PropertyRepository() {
        propertiesRef = FirebaseDatabase.getInstance().getReference(Constants.DB_PROPERTIES);
    }

    /**
     * Fetch active properties once (single shot).
     * Use this for most screens to avoid listener accumulation.
     */
    public void getActiveProperties(PropertyListCallback callback) {
        propertiesRef.orderByChild("status").equalTo("active")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onSuccess(parseList(snapshot));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    /**
     * Observe active properties in real-time. Caller MUST hold the returned listener
     * and call propertiesRef.removeEventListener(listener) in onDestroyView / onStop.
     */
    public ValueEventListener observeActiveProperties(PropertyListCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onSuccess(parseList(snapshot));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        };
        propertiesRef.orderByChild("status").equalTo("active").addValueEventListener(listener);
        return listener;
    }

    public void removeActivePropertiesListener(ValueEventListener listener) {
        propertiesRef.orderByChild("status").equalTo("active").removeEventListener(listener);
    }

    /** Fetch featured properties once (single shot). */
    public void getFeaturedProperties(PropertyListCallback callback) {
        propertiesRef.orderByChild("isFeatured").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onSuccess(parseList(snapshot));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }


    public void getProperty(String propertyId, PropertyCallback callback) {
        propertiesRef.child(propertyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Property p = snapshot.getValue(Property.class);
                if (p != null) {
                    p.setId(snapshot.getKey());
                    callback.onSuccess(p);
                } else {
                    callback.onFailure("Property not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void incrementViewCount(String propertyId) {
        propertiesRef.child(propertyId).child("viewCount").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long views = currentData.getValue(Long.class);
                if (views == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue(views + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Ignore
            }
        });
    }

    private List<Property> parseList(DataSnapshot snapshot) {
        List<Property> list = new ArrayList<>();
        for (DataSnapshot ds : snapshot.getChildren()) {
            Property p = ds.getValue(Property.class);
            if (p != null) {
                p.setId(ds.getKey());
                list.add(p);
            }
        }
        return list;
    }
}
