package com.example.houserentalapp.repository;

import androidx.annotation.NonNull;

import com.example.houserentalapp.model.Conversation;
import com.example.houserentalapp.model.Message;
import com.example.houserentalapp.utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageRepository {

    public interface ConversationCallback {
        void onSuccess(String conversationId);
        void onFailure(String error);
    }

    public interface ConversationListCallback {
        void onSuccess(List<Conversation> conversations);
        void onFailure(String error);
    }

    public interface MessageListCallback {
        void onMessageAdded(Message message);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }

    private final DatabaseReference conversationsRef;
    private final DatabaseReference messagesRef;

    public MessageRepository() {
        conversationsRef = FirebaseDatabase.getInstance()
                .getReference(Constants.DB_CONVERSATIONS);
        messagesRef = FirebaseDatabase.getInstance()
                .getReference(Constants.DB_MESSAGES);
    }

    /**
     * Get or create a conversation between two users for a given property.
     * Returns the conversation ID.
     */
    public void getOrCreateConversation(String uid1, String uid2, String propertyId,
                                         String propertyTitle, ConversationCallback callback) {
        // Query by both participant + property
        conversationsRef.orderByChild("propertyId").equalTo(propertyId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Conversation conv = ds.getValue(Conversation.class);
                            if (conv != null && conv.getParticipantIds() != null
                                    && conv.getParticipantIds().containsKey(uid1)
                                    && conv.getParticipantIds().containsKey(uid2)) {
                                callback.onSuccess(ds.getKey());
                                return;
                            }
                        }
                        // Create new conversation
                        String convId = conversationsRef.push().getKey();
                        if (convId == null) { callback.onFailure("Failed to create conversation"); return; }
                        Conversation conv = new Conversation(uid1, uid2, propertyId, propertyTitle);
                        conversationsRef.child(convId).setValue(conv)
                                .addOnSuccessListener(unused -> callback.onSuccess(convId))
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void getUserConversations(String uid, ConversationListCallback callback) {
        conversationsRef.orderByChild("participantIds/" + uid).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Conversation> list = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Conversation c = ds.getValue(Conversation.class);
                            if (c != null) { c.setId(ds.getKey()); list.add(c); }
                        }
                        callback.onSuccess(list);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void sendMessage(String conversationId, Message message, SimpleCallback callback) {
        String msgId = messagesRef.child(conversationId).push().getKey();
        if (msgId == null) { callback.onFailure("Failed to send"); return; }
        message.setId(msgId);
        messagesRef.child(conversationId).child(msgId).setValue(message)
                .addOnSuccessListener(unused -> {
                    // Update last message in conversation
                    conversationsRef.child(conversationId).child("lastMessage").setValue(message.getText());
                    conversationsRef.child(conversationId).child("lastMessageAt").setValue(message.getTimestamp());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public ChildEventListener observeMessages(String conversationId, MessageListCallback callback) {
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String prev) {
                Message msg = snapshot.getValue(Message.class);
                if (msg != null) { msg.setId(snapshot.getKey()); callback.onMessageAdded(msg); }
            }
            @Override public void onChildChanged(@NonNull DataSnapshot s, String p) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot s) {}
            @Override public void onChildMoved(@NonNull DataSnapshot s, String p) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        };
        messagesRef.child(conversationId).addChildEventListener(listener);
        return listener;
    }

    public void markMessagesRead(String conversationId, String readerUid) {
        messagesRef.child(conversationId)
                .orderByChild("isRead").equalTo(false)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Message msg = ds.getValue(Message.class);
                            if (msg != null && !readerUid.equals(msg.getSenderId())) {
                                ds.getRef().child("isRead").setValue(true);
                            }
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}
