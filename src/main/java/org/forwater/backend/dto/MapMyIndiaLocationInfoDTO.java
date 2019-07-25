package org.forwater.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties
public class MapMyIndiaLocationInfoDTO {
    private String houseNumber;
    private String houseName;
    private String poi;
    @SerializedName("poi_dist")
    private String poiDist;
    private String street;
    @SerializedName("street_dist")
    private String streetDist;
    private String subSubLocality;
    private String subLocality;
    private String locality;
    private String village;
    private String district;
    private String subDistrict;
    private String city;
    private String state;
    private String pincode;
    private String lat;
    private String lng;
    private String area;
    @SerializedName("formatted_address")
    private String formattedAddress;

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getPoiDist() {
        return poiDist;
    }

    public void setPoiDist(String poiDist) {
        this.poiDist = poiDist;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetDist() {
        return streetDist;
    }

    public void setStreetDist(String streetDist) {
        this.streetDist = streetDist;
    }

    public String getSubSubLocality() {
        return subSubLocality;
    }

    public void setSubSubLocality(String subSubLocality) {
        this.subSubLocality = subSubLocality;
    }

    public String getSubLocality() {
        return subLocality;
    }

    public void setSubLocality(String subLocality) {
        this.subLocality = subLocality;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
}
