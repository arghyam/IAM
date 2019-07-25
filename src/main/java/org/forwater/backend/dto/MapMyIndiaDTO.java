package org.forwater.backend.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapMyIndiaDTO {
    private String responseCode;
    private String version;
    @SerializedName("results")
    private List<MapMyIndiaLocationInfoDTO> locationInfoList;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<MapMyIndiaLocationInfoDTO> getLocationInfoList() {
        return locationInfoList;
    }

    public void setLocationInfoList(List<MapMyIndiaLocationInfoDTO> locationInfoList) {
        this.locationInfoList = locationInfoList;
    }
}
