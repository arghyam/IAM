package org.forwater.backend.dto;

public class CityDTO {
    String cities;
    String fKeySubDistricts;
    String osid;


    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getfKeySubDistricts() {
        return fKeySubDistricts;
    }

    public void setfKeySubDistricts(String fKeySubDistricts) {
        this.fKeySubDistricts = fKeySubDistricts;
    }

    public String getOsid() {
        return osid;
    }

    public void setOsid(String osid) {
        this.osid = osid;
    }
}
