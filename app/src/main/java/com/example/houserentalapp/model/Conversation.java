package com.example.houserentalapp.model;

import java.util.HashMap;
import java.util.Map;

public class Conversation {
    private String id;
    private Map<String, Boolean> participantIds;
    private String propertyId;
    private String propertyTitle;
    private String lastMessage;
    private long lastMessageAt;
    private long createdAt;
    // Denormalized for display
    private String otherUserName;
    private String otherUserImage;

    public Conversation() {
        participantIds = new HashMap<>();
    }

    public Conversation(String uid1, String uid2, String propertyId, String propertyTitle) {
        participantIds = new HashMap<>();
        participantIds.put(uid1, true);
        participantIds.put(uid2, true);
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.createdAt = System.currentTimeMillis();
        this.lastMessageAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Map<String, Boolean> getParticipantIds() { return participantIds; }
    public void setParticipantIds(Map<String, Boolean> participantIds) { this.participantIds = participantIds; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public String getPropertyTitle() { return propertyTitle; }
    public void setPropertyTitle(String propertyTitle) { this.propertyTitle = propertyTitle; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public long getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(long lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getOtherUserName() { return otherUserName; }
    public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
    public String getOtherUserImage() { return otherUserImage; }
    public void setOtherUserImage(String otherUserImage) { this.otherUserImage = otherUserImage; }
}
