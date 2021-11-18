package com.example.digitalbuddy;

public class Message {
    private String messageId;
    private String roomId;
    private String userId;
    private String userName;
    private String message;
    private String date;
    private String time;

    public Message(){}

    public Message(String messageId, String roomId, String userId, String userName, String message, String date, String time) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.date = date;
        this.time = time;
    }

//    getter
    public String getMessageId() {
        return messageId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

//    setter
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
