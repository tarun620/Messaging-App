package com.example.idene.whatsapp.model;

//COMPLETED

public class Message {

    //Mensagem--Message

    private String idUser;
    private String name;
    private String message;
    private String image;

    public Message() {
        this.setName("");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
