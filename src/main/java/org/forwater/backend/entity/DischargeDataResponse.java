package org.forwater.backend.entity;

public class DischargeDataResponse extends  DischargeData {

    private String osid;

    private String submittedBy;

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }

    public String getSubmittedby() {
        return submittedBy;
    }

    public void setSubmittedby(String submittedby) {
        this.submittedBy = submittedby;
    }
}
