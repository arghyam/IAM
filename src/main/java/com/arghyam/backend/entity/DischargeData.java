package com.arghyam.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties
public class DischargeData {

    private String springCode;

    private String springName;

    private String userId;

    private List<String> dischargeTime;

    private String createdDate;

    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }

    public String getSpringName() {
        return springName;
    }

    public void setSpringName(String springName) {
        this.springName = springName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getDischargeTime() {
        return dischargeTime;
    }

    public void setDischargeTime(List<String> dischargeTime) {
        this.dischargeTime = dischargeTime;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}

