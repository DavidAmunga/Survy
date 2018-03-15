package com.buttercell.survy.model;

import java.io.Serializable;

/**
 * Created by amush on 13-Feb-18.
 */

public class Student implements Serializable {
    private String course,dateJoined,email,image,name;

    public Student(String course, String dateJoined, String email, String image, String name) {
        this.course = course;
        this.dateJoined = dateJoined;
        this.email = email;
        this.image = image;
        this.name = name;
    }

    public Student() {
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

