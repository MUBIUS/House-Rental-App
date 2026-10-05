package com.example.houserentalapp.model;

public class Message {
    private String id;
    private String conversationId;
    private String senderId;
    private String text;
    private String imageUrl;
    private boolean isRead;
    private long timestamp;

    public Message() { }

    public Message(String conversationId, String senderId, String text) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.text = text;
        this.isRead = false;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
