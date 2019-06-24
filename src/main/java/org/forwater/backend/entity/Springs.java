package org.forwater.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Springs {

    private String springCode;

    private String springName;

    private String userId;

    private String tenantId;

    private String orgId;

    private double latitude;

    private double longitude;

    private double elevation;

    private double accuracy;

    private String village;

    private Integer numberOfHouseholds;

    private String ownershipType;

    private List<String> usage;

    private List<String> images;

    private Map<String, Object> extraInformation;

    private String createdTimeStamp;

    private String updatedTimeStamp;


    public String getSpringName() {
        return springName;
    }

    public void setSpringName(String springName) {
        this.springName = springName;
    }


    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }



    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public Integer getNumberOfHouseholds() {
        return numberOfHouseholds;
    }

    public void setNumberOfHouseholds(Integer numberOfHouseholds) {
        this.numberOfHouseholds = numberOfHouseholds;
    }

    public String getOwnershipType() {
        return ownershipType;
    }

    public void setOwnershipType(String ownershipType) {
        this.ownershipType = ownershipType;
    }

    public List<String> getUsage() {
        return usage;
    }

    public void setUsage(List<String> usage) {
        this.usage = usage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(Map<String, Object> extraInformation) {
        this.extraInformation = extraInformation;
    }

    public String getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(String createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getUpdatedTimeStamp() {
        return updatedTimeStamp;
    }

    public void setUpdatedTimeStamp(String updatedTimeStamp) {
        this.updatedTimeStamp = updatedTimeStamp;
    }
}
