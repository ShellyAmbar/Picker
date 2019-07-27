package com.example.picker.Models;

public class ContactModel {
    private String pictureUrl;
    private String contactName;
    private String contactNumber;
    private String userFrom;

    public ContactModel(String pictureUrl, String contactName, String contactNumber, String userFrom) {
        this.pictureUrl = pictureUrl;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.userFrom = userFrom;
    }

    public ContactModel() {
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
