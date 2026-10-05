package com.example.houserentalapp.model;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceTicket {
    private String id;
    private String leaseId;
    private String propertyId;
    private String propertyTitle;
    private String tenantId;
    private String tenantName;
    private String landlordId;
    private String title;
    private String description;
    private String category;   // plumbing | electrical | appliance | structural | cleaning | other
    private String priority;   // low | medium | high | urgent
    private String status;     // open | in_progress | resolved | closed
    private List<String> imageUrls;
    private String landlordNotes;
    private long createdAt;
    private long updatedAt;
    private long resolvedAt;

    public MaintenanceTicket() {
        imageUrls = new ArrayList<>();
        status = "open";
        priority = "medium";
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLeaseId() { return leaseId; }
    public void setLeaseId(String leaseId) { this.leaseId = leaseId; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public String getLandlordNotes() { return landlordNotes; }
    public void setLandlordNotes(String landlordNotes) { this.landlordNotes = landlordNotes; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public long getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(long resolvedAt) { this.resolvedAt = resolvedAt; }
}
