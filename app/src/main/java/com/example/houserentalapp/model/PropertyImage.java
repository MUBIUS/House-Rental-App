package com.example.houserentalapp.model;

public class PropertyImage {
    private String id;
    private String propertyId;
    private String url;
    private String type;   // image | video | floorplan
    private long uploadedAt;

    public PropertyImage() { }

    public PropertyImage(String propertyId, String url, String type) {
        this.propertyId = propertyId;
        this.url = url;
        this.type = type;
        this.uploadedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(long uploadedAt) { this.uploadedAt = uploadedAt; }
}
