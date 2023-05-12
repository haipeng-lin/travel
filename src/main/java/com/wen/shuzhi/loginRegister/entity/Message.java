package com.wen.shuzhi.loginRegister.entity;


public class Message {
    private String msg;
    private User user;

    public Message() {
    }

    public Message(String msg, User user) {
        this.msg = msg;
        this.user = user;
    }
    public Message(String msg) {
        this.msg = msg;
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msg='" + msg + '\'' +
                ", user=" + user +
                '}';
    }
}
