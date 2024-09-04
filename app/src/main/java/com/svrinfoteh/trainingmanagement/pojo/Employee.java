package com.svrinfoteh.trainingmanagement.pojo;

import java.io.Serializable;

public class Employee implements Serializable {

    String name,subject;

    public Employee() {
    }

    public Employee(String name, String subject) {
        this.name = name;
        this.subject = subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
