package com.example.communique.helpers;

public class User {
    private String uid, userName, userEmail, userImage, userPhone;

    public User(String uid, String userName, String userEmail, String userImage, String userPhone){
        this.uid = uid;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImage = userImage;
        this.userPhone = userPhone;
    }

    public User(String userPhone){
        this.userPhone = userPhone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
