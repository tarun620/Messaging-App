package com.example.idene.whatsapp.model;

import com.example.idene.whatsapp.config.ConfigurationFirebase;
import com.example.idene.whatsapp.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

//COMPLETED

public class User implements Serializable { //implements Serializable: permite passar objeto-lets you pass object

    private String id;
    private String name;
    private String email;
    private String password;
    private String Photograph;


    public User() {
    }

    public void toSave(){

        DatabaseReference firebaseref = ConfigurationFirebase.getFirebaseDatabase();
        DatabaseReference user = firebaseref.child("users").child(getId());

        user.setValue(this);//salvar o objeto -save the object
    }
    public void update(){


            String user_identifier = UserFirebase.getIdentifierUser();
        DatabaseReference database = ConfigurationFirebase.getFirebaseDatabase();

        DatabaseReference userRef = database.child("users")
                .child(user_identifier);

        //para fazer update tenho que criar um map--to update I have to create a map
        Map<String,Object> valuesUsers = converterParaMap();
        userRef.updateChildren(valuesUsers);

    }

    @Exclude
    public Map<String,Object> converterParaMap(){

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("email",getEmail());
        userMap.put("nome", getName());
        userMap.put("foto", getPhotograph());

        return  userMap;
    }

    public String getPhotograph() {
        return Photograph;
    }

    public void setPhotograph(String photograph) {
        this.Photograph = photograph;
    }

    public String getName() {
        return name;
    }
    @Exclude//remove o id pois ja temos essa informação--remove the id because we already have this information

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() { return email;}

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude //remove a senha-password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
