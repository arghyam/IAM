package org.forwater.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.forwater.backend.dto.SpringLocation;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Springs extends SpringLocation implements Comparable<Springs> {

    private String springCode;

    private String springName;

    private String userId;

    private String tenantId;

    private String orgId;

    private Integer numberOfHouseholds;

    private String ownershipType;

    private List<String> usage;

    private List<String> images;

    private Map<String, Object> extraInformation;

    private String createdTimeStamp;

    private String updatedTimeStamp;

    private String submittedBy;

    private String address;

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int compareTo(Springs spring) {
        if (getCreatedTimeStamp() == null || spring.getCreatedTimeStamp() == null)
            return 0;
        return getCreatedTimeStamp().compareTo(spring.getCreatedTimeStamp());
    }
}
