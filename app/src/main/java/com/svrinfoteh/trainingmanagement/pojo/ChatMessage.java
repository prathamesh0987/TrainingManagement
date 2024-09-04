package com.svrinfoteh.trainingmanagement.pojo;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String message,user;
    private Long dateTime;

    public ChatMessage() {

    }

    public ChatMessage(String message, String user, Long dateTime) {
        this.message = message;
        this.user = user;
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

}
