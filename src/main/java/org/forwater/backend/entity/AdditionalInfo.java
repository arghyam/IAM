package org.forwater.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalInfo {
    private String springCode;
    private String seasonality;
    private List<String> months;
    private List<String> usage;
    private Integer numberOfHousehold;

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

    public List<String> getMonths() {
        return months;
    }

    public void setMonths(List<String> months) {
        this.months = months;
    }

    public Integer getNumberOfHousehold() {
        return numberOfHousehold;
    }

    public void setNumberOfHousehold(Integer numberOfHousehold) {
        this.numberOfHousehold = numberOfHousehold;
    }

    public List<String> getUsage() {
        return usage;
    }

    public void setUsage(List<String> usage) {
        this.usage = usage;
    }
}
