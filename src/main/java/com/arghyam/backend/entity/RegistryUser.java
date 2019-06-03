package com.arghyam.backend.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistryUser {

    private String firstName;

    private String lastName;

    private String emailId;

    private String salutation;

    private String phoneNumber;

    private String photo;

    private String userId;

    private String crtdDttm;

    private String updtDttm;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCrtdDttm() {
        return crtdDttm;
    }

    public void setCrtdDttm(String crtdDttm) {
        this.crtdDttm = crtdDttm;
    }

    public String getUpdtDttm() {
        return updtDttm;
    }

    public void setUpdtDttm(String updtDttm) {
        this.updtDttm = updtDttm;
    }

    public RegistryUser() {
    }

    public RegistryUser(String firstName, String lastName, String emailId, String salutation, String userId, String crtdDttm, String updtDttm, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = "";
        this.emailId = "";
        this.salutation = salutation;
        this.userId = userId;
        this.crtdDttm = crtdDttm;
        this.updtDttm = updtDttm;
        this.photo = "";
        this.phoneNumber = phoneNumber;
    }
}