package org.forwater.backend.service;

import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    LoginAndRegisterResponseMap postStates(RequestDTO requestDTO) throws IOException;

    LoginAndRegisterResponseMap postDistricts( RequestDTO requestDTO, String districtName, String fKeyState) throws IOException;

    LoginAndRegisterResponseMap postSubDistricts( RequestDTO requestDTO, String subDistrictName, String fKeyDistrict) throws IOException;

    LoginAndRegisterResponseMap postVillage(RequestDTO requestDTO, String village, String fKeySubDistrict) throws IOException;

    LoginAndRegisterResponseMap getStates(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getDistricts(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getSubDistricts(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getStateByName(RequestDTO requestDTO) throws IOException;

    String getStateOsidByName(RequestDTO requestDTO, String stateName) throws IOException;

    String getDistrictOsidByDistrictName(RequestDTO requestDTO, String district, String fKeyState) throws IOException;

    String getSubDistrictOsidBySubDistrictName(RequestDTO requestDTO, String subDistrict, String stateOsid) throws IOException;

    LoginAndRegisterResponseMap getDistrictsByStateOSID (RequestDTO requestDTO) throws IOException;

    LoginAndRegisterResponseMap getSubDistrictsByDistrictOSID (RequestDTO requestDTO) throws IOException;

    LoginAndRegisterResponseMap getVillagesBySubDistrictOSID (RequestDTO requestDTO) throws IOException;

}
