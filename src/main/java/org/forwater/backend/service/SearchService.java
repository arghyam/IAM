package org.forwater.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;

import java.io.IOException;

public interface SearchService {
    LoginAndRegisterResponseMap postStates(RequestDTO requestDTO) throws IOException;

    LoginAndRegisterResponseMap postDistricts( RequestDTO requestDTO, String districtName, String fKeyState) throws IOException;

    LoginAndRegisterResponseMap getStates(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getDistricts(RequestDTO requestDTO,String flag) throws IOException;

    LoginAndRegisterResponseMap getStateByName(RequestDTO requestDTO) throws IOException;

    String getStateOsidByName(RequestDTO requestDTO, String stateName) throws IOException;

    LoginAndRegisterResponseMap getDistrictsByStateOSID (RequestDTO requestDTO) throws IOException;

}
