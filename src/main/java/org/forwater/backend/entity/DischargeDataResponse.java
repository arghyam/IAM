package org.forwater.backend.entity;

public class DischargeDataResponse extends  DischargeData {

    private String osid;

    private String submittedby;

//    private String springName;

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }


    public String getSubmittedby() {
        return submittedby;
    }

    public void setSubmittedby(String submittedby) {
        this.submittedby = submittedby;
    }

//    public String getSpringName() {
//        return springName;
//    }
//
//    public void setSpringName(String springName) {
//        this.springName = springName;
//    }
}
