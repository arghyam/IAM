package org.forwater.backend.dto;

public class NotificationDTOEntity {
    private String userId;
    private String springCode;
    private String dischargeDataOsid;
    private String status;
    private String firstName;
    private long createdAt;
    private String osid;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpringCode() {
        return springCode;
    }

    public void setSpringCode(String springCode) {
        this.springCode = springCode;
    }

    public String getDischargeDataOsid() {
        return dischargeDataOsid;
    }

    public void setDischargeDataOsid(String dischargeDataOsid) {
        this.dischargeDataOsid = dischargeDataOsid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }
}
