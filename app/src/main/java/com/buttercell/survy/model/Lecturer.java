package com.buttercell.survy.model;

/**
 * Created by amush on 13-Feb-18.
 */

public class Lecturer {
    private String email,image,name;

    public Lecturer(String email, String image, String name) {
        this.email = email;
        this.image = image;
        this.name = name;
    }

    public Lecturer() {
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
