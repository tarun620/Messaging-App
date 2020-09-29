package com.example.idene.whatsapp.model;

import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.google.firebase.database.DatabaseReference;

//COMPLETED

public class Conversation {

    //Conversa--Conversation

    private String idSender;
    private String idRecipient;
    private String lastMessage;
    private User userExhibition;
    private String isGroup;
    private Group group;


    public Conversation() {
        this.setIsGroup("false");
    }

    public void toSave(){

        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference conversationRef = database.child("conversation");
        conversationRef.child(this.getIdSender())
                .child(this.getIdRecipient())
                .setValue(this);

    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdRecipient() {
        return idRecipient;
    }

    public void setIdRecipient(String idRecipient) {
        this.idRecipient = idRecipient;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUserExhibition() {
        return userExhibition;
    }

    public void setUserExhibition(User userExhibition) {
        this.userExhibition = userExhibition;
    }
}
