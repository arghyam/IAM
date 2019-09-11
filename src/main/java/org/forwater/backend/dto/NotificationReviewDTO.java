package org.forwater.backend.dto;

import javax.swing.*;

public class NotificationReviewDTO {

    private String status;

    private String notificationOsid;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotificationOsid() {
        return notificationOsid;
    }

    public void setNotificationOsid(String notificationOsid) {
        this.notificationOsid = notificationOsid;
    }
}
