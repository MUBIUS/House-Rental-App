package com.example.houserentalapp.model;

public class Payment {
    private String id;
    private String leaseId;
    private String propertyId;
    private String tenantId;
    private String landlordId;
    private double amount;
    private String currency;
    private long dueDate;
    private long paidDate;
    private String status;        // due | paid | overdue | waived
    private String method;        // cash | bank_transfer | upi | card | other
    private String transactionId;
    private String notes;
    private long createdAt;

    public Payment() {
        status = "due";
        createdAt = System.currentTimeMillis();
    }

    public Payment(String leaseId, String propertyId, String tenantId, String landlordId,
                   double amount, String currency, long dueDate) {
        this();
        this.leaseId = leaseId;
        this.propertyId = propertyId;
        this.tenantId = tenantId;
        this.landlordId = landlordId;
        this.amount = amount;
        this.currency = currency;
        this.dueDate = dueDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLeaseId() { return leaseId; }
    public void setLeaseId(String leaseId) { this.leaseId = leaseId; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCurrency() { return currency != null ? currency : "INR"; }
    public void setCurrency(String currency) { this.currency = currency; }
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    public long getPaidDate() { return paidDate; }
    public void setPaidDate(long paidDate) { this.paidDate = paidDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isPaid() { return "paid".equals(status); }
    public boolean isOverdue() {
        return "due".equals(status) && System.currentTimeMillis() > dueDate;
    }
}
