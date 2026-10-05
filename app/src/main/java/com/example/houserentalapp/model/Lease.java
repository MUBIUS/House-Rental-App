package com.example.houserentalapp.model;

public class Lease {
    private String id;
    private String propertyId;
    private String propertyTitle;
    private String propertyAddress;
    private String propertyImage;
    private String tenantId;
    private String tenantName;
    private String landlordId;
    private String landlordName;
    private long startDate;
    private long endDate;
    private double monthlyRent;
    private String currency;
    private double deposit;
    private String status;        // active | expired | terminated
    private String documentUrl;   // signed lease document
    private long createdAt;
    private long updatedAt;

    public Lease() {
        status = "active";
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
    public String getPropertyAddress() { return propertyAddress; }
    public void setPropertyAddress(String propertyAddress) { this.propertyAddress = propertyAddress; }
    public String getPropertyImage() { return propertyImage; }
    public void setPropertyImage(String propertyImage) { this.propertyImage = propertyImage; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public String getLandlordName() { return landlordName; }
    public void setLandlordName(String landlordName) { this.landlordName = landlordName; }
    public long getStartDate() { return startDate; }
    public void setStartDate(long startDate) { this.startDate = startDate; }
    public long getEndDate() { return endDate; }
    public void setEndDate(long endDate) { this.endDate = endDate; }
    public double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }
    public String getCurrency() { return currency != null ? currency : "INR"; }
    public void setCurrency(String currency) { this.currency = currency; }
    public double getDeposit() { return deposit; }
    public void setDeposit(double deposit) { this.deposit = deposit; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return "active".equals(status); }
    public boolean isExpired() { return "expired".equals(status) ||
            (endDate > 0 && System.currentTimeMillis() > endDate); }
}
