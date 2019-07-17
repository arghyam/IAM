package org.forwater.backend.entity;

public class Reviewer {

    private String osid;

    private String reviewerId;

    private String status;

    private String notificationOsid;

    private String submittedBy;


    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

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


    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }
}
