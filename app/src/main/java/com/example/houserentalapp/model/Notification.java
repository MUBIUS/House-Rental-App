package com.example.houserentalapp.model;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String id;
    private String type;     // see Constants.NOTIF_*
    private String title;
    private String body;
    private Map<String, String> data;
    private boolean isRead;
    private long createdAt;

    public Notification() {
        data = new HashMap<>();
        isRead = false;
        createdAt = System.currentTimeMillis();
    }

    public Notification(String type, String title, String body) {
        this();
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public Map<String, String> getData() { return data; }
    public void setData(Map<String, String> data) { this.data = data; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
