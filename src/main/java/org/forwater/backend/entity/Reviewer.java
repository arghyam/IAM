package org.forwater.backend.entity;

public class Reviewer {

    private String osid;

    private String userId;

    private String status;

    private String notificationOsid;

    public String getNotificationOsid() {
        return notificationOsid;
    }

    public void setNotificationOsid(String notificationOsid) {
        this.notificationOsid = notificationOsid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }
}
