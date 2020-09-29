package com.example.idene.whatsapp.model;

import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

//COMPLETED

public class Group implements Serializable {

    //Grupo-Group

    private String id;
    private String name;
    private String photograph;
    private List<User> members;

    public Group() {

        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference groupRef = database.child("groups");//criando referencia no firebase-creating reference in firebase

        String idGroupFirebase = groupRef.push().getKey();//gerar chavr--generate key
        setId(idGroupFirebase);

    }

    public void toSave(){
        //salvar--to save

        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference groupRef = database.child("groups");//criando referencia no firebase--creating reference in firebase

        groupRef.child(getId()).setValue(this);

        //salvar conversas para membros do grupo--// save conversations for group members
        for (User member: getMembers()){

            String sender = Base64Custom.encodeBase64(member.getEmail());
            String idRecipient = getId();

            Conversation conversation = new Conversation();
            conversation.setIdSender(sender);
            conversation.setIdRecipient(idRecipient);
            conversation.setLastMessage("");
            conversation.setIsGroup("true");
            conversation.setGroup(this);

            conversation.toSave();

        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotograph() {
        return photograph;
    }

    public void setPhotograph(String photograph) {
        this.photograph = photograph;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
