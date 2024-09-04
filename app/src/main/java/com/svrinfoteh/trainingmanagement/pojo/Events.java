package com.svrinfoteh.trainingmanagement.pojo;

import java.io.Serializable;

public class Events implements Serializable {
    private String title,description,image;
    private int likes;
    public Events() {

    }

    public Events(String title, String description, String image, int likes) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.likes=likes;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public float getLikes() { return likes; }
}
