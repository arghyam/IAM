package org.forwater.backend.service;

import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    LoginAndRegisterResponseMap postStates(RequestDTO requestDTO) throws IOException;

    LoginAndRegisterResponseMap postDistricts( RequestDTO requestDTO, String districtName, String fKeyState) throws IOException;

    LoginAndRegisterResponseMap postSubDistricts( RequestDTO requestDTO, String subDistrictName, String fKeyDistrict) throws IOException;

    LoginAndRegisterResponseMap getStates(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getDistricts(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getSubDistrictsOsid(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getSubDistricts(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getStateByName(RequestDTO requestDTO) throws IOException;

    String getStateOsidByName(RequestDTO requestDTO, String stateName) throws IOException;

    String getDistrictOsidByDistrictName(RequestDTO requestDTO, String district, String fKeyState) throws IOException;

    LoginAndRegisterResponseMap getDistrictsByStateOSID (RequestDTO requestDTO) throws IOException;

    LoginAndRegisterResponseMap getSubDistrictsByDistrictOSID (RequestDTO requestDTO) throws IOException;

    String getsubDistrictOsid(RequestDTO requestDTO, String subDistrict, String fKeyDistrict) throws IOException;

    LoginAndRegisterResponseMap postCities(RequestDTO requestDTO, String cities, String subDistrictOsid) throws IOException;

    LoginAndRegisterResponseMap getCities(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getCitiesBySubDistrictOSID(RequestDTO requestDTO) throws IOException;
}
