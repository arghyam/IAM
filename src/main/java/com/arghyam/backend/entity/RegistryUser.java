package com.arghyam.backend.entity;

public class RegistryUser {

    public String firstName;

    public String lastName;

    public String emailId;

    public String salutation;

    public String crtdDttm;

    public String updtDttm;

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
}
