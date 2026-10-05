package com.example.houserentalapp.model;

public class Report {
    private String id;
    private String reporterId;
    private String reporterName;
    private String targetType;   // property | user
    private String targetId;
    private String reason;
    private String description;
    private String status;       // pending | reviewed | actioned
    private long createdAt;
    private long updatedAt;

    public Report() {
        status = "pending";
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    public Report(String reporterId, String reporterName, String targetType,
                  String targetId, String reason, String description) {
        this();
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
