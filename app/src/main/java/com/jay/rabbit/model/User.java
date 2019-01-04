package com.jay.rabbit.model;

public class User {

    private String id;
    private String userName;
    private String userImage;
    private String status;
    private String search;


    public User() {}


    public User(String id, String userName, String userImage, String status, String search) {
        this.id = id;
        this.userName = userName;
        this.userImage = userImage;
        this.status = status;
        this.search = search;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
