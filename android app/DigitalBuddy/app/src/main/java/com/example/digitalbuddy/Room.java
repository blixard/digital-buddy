package com.example.digitalbuddy;

import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String roomName;
    private String users;
    private String roomId;
    private String password;

    public Room(){}
    public Room(String id,String roomName, String users, String roomId, String password) {
        this.id = id;
        this.users = users;
        this.roomName = roomName;
        this.roomId = roomId;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getUsers() {
        return users;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getPassword() {
        return password;
    }

//    setter
    public void setId(String id) {
        this.id = id;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
