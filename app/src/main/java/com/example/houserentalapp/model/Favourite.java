package com.example.houserentalapp.model;

public class Favourite {
    private String propertyId;
    private long savedAt;

    public Favourite() { }

    public Favourite(String propertyId) {
        this.propertyId = propertyId;
        this.savedAt = System.currentTimeMillis();
    }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public long getSavedAt() { return savedAt; }
    public void setSavedAt(long savedAt) { this.savedAt = savedAt; }
}
