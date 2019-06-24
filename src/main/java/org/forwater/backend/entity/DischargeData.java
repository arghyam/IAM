package org.forwater.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DischargeData {

    private String springCode;

    private List<String> dischargeTime;

    private Double volumeOfContainer;

    private List<Double> litresPerSecond;

    private String userId;

    private String status;

    private String seasonality;

    private List<String> months;

    private String tenantId;

    private String orgId;

    private List<String> images;

    private String createdTimeStamp;

    private String updatedTimeStamp;

    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }

    public List<String> getDischargeTime() {
        return dischargeTime;
    }

    public void setDischargeTime(List<String> dischargeTime) {
        this.dischargeTime = dischargeTime;
    }

    public double getVolumeOfContainer() {
        return volumeOfContainer;
    }

    public void setVolumeOfContainer(Double volumeOfContainer) {
        this.volumeOfContainer = volumeOfContainer;
    }

    public List<Double> getLitresPerSecond() {
        return litresPerSecond;
    }

    public void setLitresPerSecond(List<Double> litresPerSecond) {
        this.litresPerSecond = litresPerSecond;
    }

    public void setMonths(List<String> months) {
        this.months = months;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSeasonality() {
        return seasonality;
    }

    public void setSeasonality(String seasonality) {
        this.seasonality = seasonality;
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

    public List<String> getMonths() {
        return months;
    }

    public List<String> getImages() {
        return images;
    }
}

