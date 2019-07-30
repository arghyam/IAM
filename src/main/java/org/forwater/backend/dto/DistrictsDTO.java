package org.forwater.backend.dto;


public class DistrictsDTO {
    private String districts;
    private String osid;
    private String fKeyState;

    public String getOsid() {
        return osid;
    }
    public void setOsid(String osid) {
        this.osid = osid;
    }
    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }
    public String getfKeyState() {
        return fKeyState;
    }

    public void setfKeyState(String fKeyState) {
        this.fKeyState = fKeyState;
    }
}
