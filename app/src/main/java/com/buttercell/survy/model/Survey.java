package com.buttercell.survy.model;

/**
 * Created by amush on 19-Feb-18.
 */

public class Survey {
    private String desc, image, name, sem, userId, userName, year,date;
    long timestamp;

    public Survey() {
    }

    public Survey(String desc, String image, String name, String sem, String userId, String userName, String year, String date, long timestamp) {
        this.desc = desc;
        this.image = image;
        this.name = name;
        this.sem = sem;
        this.userId = userId;
        this.userName = userName;
        this.year = year;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getSem() {
        return sem;
    }

    public void setSem(String sem) {
        this.sem = sem;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
