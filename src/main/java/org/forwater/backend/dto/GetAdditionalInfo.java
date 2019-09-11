package org.forwater.backend.dto;

import java.util.List;

public class GetAdditionalInfo {

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

    public List<String> getUsage() {
        return usage;
    }

    public void setUsage(List<String> usage) {
        this.usage = usage;
    }

    public Integer getNumberOfHousehold() {
        return numberOfHousehold;
    }

    public void setNumberOfHousehold(Integer numberOfHousehold) {
        this.numberOfHousehold = numberOfHousehold;
    }
}
