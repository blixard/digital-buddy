package com.example.digitalbuddy;

public class Chat {
    private String msg;
    private String usrename;
    private String userid;
    private String datetime;

    public Chat(){}
    public Chat(String msg, String usrename, String userid, String datetime) {
        this.msg = msg;
        this.usrename = usrename;
        this.userid = userid;
        this.datetime = datetime;
    }

    public String getMsg() {
        return msg;
    }

    public String getUsrename() {
        return usrename;
    }

    public String getUserid() {
        return userid;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setUsrename(String usrename) {
        this.usrename = usrename;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
