package org.forwater.backend.dto;

public class NotificationDTO {
    private String userId;
    private String springCode;
    private String dischargeDataOsid;
    private String status;
    private String springName;
    private String userName;
    private long createdAt;

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

    public String getSpringName() {
        return springName;
    }

    public void setSpringName(String springName) {
        this.springName = springName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
