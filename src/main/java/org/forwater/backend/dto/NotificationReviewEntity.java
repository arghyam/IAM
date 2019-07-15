package org.forwater.backend.dto;

public class NotificationReviewEntity {
    private String status;
    private String osid;

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
