package com.arghyam.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalInfo {
    private String springCode;
    private String seasonality;
    private int[] months;
    private List<String> waterUseList;
    private String household;

    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }

    public String getSeasonality() {
        return seasonality;
    }

    public void setSeasonality(String seasonality) {
        this.seasonality = seasonality;
    }

    public int[] getMonths() {
        return months;
    }

    public void setMonths(int[] months) {
        this.months = months;
    }

    public String getHousehold() {
        return household;
    }

    public void setHousehold(String household) {
        this.household = household;
    }


    public List<String> getWaterUseList() {
        return waterUseList;
    }

    public void setWaterUseList(List<String> waterUseList) {
        this.waterUseList = waterUseList;
    }
}
