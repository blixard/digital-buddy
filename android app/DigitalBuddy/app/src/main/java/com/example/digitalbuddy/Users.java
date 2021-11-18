package com.example.digitalbuddy;

import java.io.Serializable;

public class Users implements Serializable {
    public String personName;
    public String personGivenName;
    public String personFamilyName;
    public String personEmail;
    public String personId;
    public String personPhoto;
    public String messages= "";
    public String rooms = "chatbot;allchat";

    public Users() {
    }

    public Users(String personName, String personGivenName, String personFamilyName, String personEmail, String personId, String personPhoto) {
        this.personName = personName;
        this.personGivenName = personGivenName;
        this.personFamilyName = personFamilyName;
        this.personEmail = personEmail;
        this.personId = personId;
        this.personPhoto = personPhoto;
    }

//    getter
    public String getPersonName() {
        return personName;
    }

    public String getPersonGivenName() {
        return personGivenName;
    }

    public String getPersonFamilyName() {
        return personFamilyName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPersonPhoto() {
        return personPhoto;
    }

    public String getMessages() {
        return messages;
    }

    public String getRooms() {
        return rooms;
    }

//    setter
    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonGivenName(String personGivenName) {
        this.personGivenName = personGivenName;
    }

    public void setPersonFamilyName(String personFamilyName) {
        this.personFamilyName = personFamilyName;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setPersonPhoto(String personPhoto) {
        this.personPhoto = personPhoto;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public void setRooms(String rooms) {
        this.rooms = rooms;
    }

//    custom methods
//    method to add personal chat room of the user to rooms
    public void addPersonalRoom(String userId){
        rooms = rooms + ";"+userId;
    }
}
