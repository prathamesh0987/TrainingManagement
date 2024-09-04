package com.svrinfoteh.trainingmanagement.pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String name,mobile,username,status;
    private ArrayList<String> course;
    public User() {
    }

    public User(String name, String mobile, String username, String status, ArrayList<String> course) {
        this.name = name;
        this.mobile = mobile;
        this.username = username;
        this.status = status;
        this.course = course;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getCourse() {
        return course;
    }

    public void setCourse(ArrayList<String> course) {
        this.course = course;
    }
}