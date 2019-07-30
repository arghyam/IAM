package org.forwater.backend.service.ServiceImpl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.org.apache.regexp.internal.RE;
import org.forwater.backend.config.AppContext;
import org.forwater.backend.dao.KeycloakService;
import org.forwater.backend.dao.RegistryDAO;
import org.forwater.backend.dto.*;
import org.forwater.backend.entity.Springs;
import org.forwater.backend.exceptions.InternalServerException;
import org.forwater.backend.service.SearchService;
import org.forwater.backend.utils.Constants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

import static org.keycloak.util.JsonSerialization.mapper;

@Component
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    KeycloakService keycloakService;

    @Autowired
    AppContext appContext;

    @Autowired
    RegistryDAO registryDAO;

    private static Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);


    /**
     * this api is for internal purpose to post states in the database
     *
     * @param requestDTO
     * @return
     * @throws IOException
     */

    @Override
    public LoginAndRegisterResponseMap postStates(RequestDTO requestDTO) throws IOException {
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        States states = new States();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("states")) {
            states = mapper.convertValue(requestDTO.getRequest().get("states"), States.class);
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("states", states);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.createUser(adminToken, registryRequest);
            loginResponseDTOCall.execute();

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }

        return null;
    }

    /**
     * this api is for posting the districts in the database
     * @return
     * @throws IOException
     */
    @Override
    public LoginAndRegisterResponseMap postDistricts( RequestDTO requestDTO , String districtName, String fKeyState) throws IOException {
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Boolean flag = true;
        List<DistrictsDTO> districtDTOList = null;
        List<String> districtsList = new ArrayList<>();
        DistrictsDTO districtsDTO = new DistrictsDTO();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("districts")) {
            districtsDTO = mapper.convertValue(requestDTO.getRequest().get("districts"), DistrictsDTO.class);
        }
        fKeyState=fKeyState.substring(2);

        districtsDTO.setDistricts(districtName);
        districtsDTO.setfKeyState(fKeyState);
        HashMap<String, Object> map = new HashMap<>();
        map.put("districts", districtsDTO);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        LoginAndRegisterResponseMap a = getDistricts(requestDTO,"2");
        Map<String, Object> statesMap = a.getResponse();
        if (statesMap.containsKey("districts")) {
             districtDTOList = (List<DistrictsDTO>) statesMap.get("districts");
        }
        for (int i = 0; i < districtDTOList.size(); i++) {
            districtsList.add(districtDTOList.get(i).getDistricts());
        }
        for (int i = 0; i < districtsList.size(); i++) {
            if (districtsList.get(i).equals(districtName)){
                flag = false;
            }
        }
        if (flag){
            try {
                Call<RegistryResponse> loginResponseDTOCall = registryDAO.createUser(adminToken, registryRequest);
                loginResponseDTOCall.execute();

            } catch (Exception e) {
                log.error("Error creating registry entry : {} ", e.getMessage());
                throw new InternalServerException("Internal server error");

            }
        }
//        for (int i = 0; i <= districtDTOList.size(); i++) {
//            if (districtDTOList.size()>0&&!districtDTOList.get(i).getDistricts().contains(districtName)) {
//                try {
//                    Call<RegistryResponse> loginResponseDTOCall = registryDAO.createUser(adminToken, registryRequest);
//                    loginResponseDTOCall.execute();
//
//                } catch (Exception e) {
//                    log.error("Error creating registry entry : {} ", e.getMessage());
//                    throw new InternalServerException("Internal server error");
//
//                }
//            }
//            else if (districtDTOList.size()==0){
//                try {
//                    Call<RegistryResponse> loginResponseDTOCall = registryDAO.createUser(adminToken, registryRequest);
//                    loginResponseDTOCall.execute();
//
//                } catch (Exception e) {
//                    log.error("Error creating registry entry : {} ", e.getMessage());
//                    throw new InternalServerException("Internal server error");
//
//                }
//            }
//        }

        return null;
    }


    /**
     * This api gives all the states from the Db
     * @param requestDTO
     * @param flag
     * @return
     * @throws IOException
     */

    @Override
    public LoginAndRegisterResponseMap getStates(RequestDTO requestDTO, String flag) throws IOException {
        retrofit2.Response<RegistryResponse> registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Map<String, String> statesMap = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("states")) {
            statesMap.put("@type", "states");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("states", statesMap);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
            } else {
                return generateStatesResponse(requestDTO, registryUserCreationResponse, flag);
            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return null;
    }

    /**
     * This api returns all the districts available from the Db
     * @param requestDTO
     * @param flag
     * @return
     * @throws IOException
     */
    @Override
    public LoginAndRegisterResponseMap getDistricts(RequestDTO requestDTO, String flag) throws IOException {
        retrofit2.Response<RegistryResponse> registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Map<String, String> districtsMap = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("districts")) {
            districtsMap.put("@type", "districts");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("districts", districtsMap);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
            } else {
                return generateDistrictsResponse(requestDTO, registryUserCreationResponse, flag);
            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return null;
    }


    /**
     * This method returns state entity by name
     *
     * @param requestDTO
     * @return
     * @throws IOException
     */
    @Override
    public LoginAndRegisterResponseMap getStateByName(RequestDTO requestDTO) throws IOException {
        List<StatesDTO> statesDTOList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> responseMap = new HashMap<>();
        LoginAndRegisterResponseMap response = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = getStates(requestDTO, "2");
        Map<String, Object> statesMap = loginAndRegisterResponseMap.getResponse();
        if (statesMap.containsKey("states")) {
            statesDTOList = (List<StatesDTO>) statesMap.get("states");
        }
        if (!statesDTOList.isEmpty()) {
            States statesDTO = mapper.convertValue(requestDTO.getRequest().get("states"), States.class);
            log.info(statesDTO.getStates());
            for (int i = 0; i < statesDTOList.size(); i++) {
                if (statesDTOList.get(i).getStates().toLowerCase().contains(statesDTO.getStates().toLowerCase())) {
                    map.put("state", statesDTOList.get(i));
                }
            }
            responseMap.put("responseObject", map);
            response.setId(requestDTO.getId());
            response.setVer(requestDTO.getVer());
            response.setEts(requestDTO.getEts());
            response.setParams(requestDTO.getParams());
            response.setResponse(responseMap);
            return response;
        } else {
            log.error("empty list");
        }
        return null;
    }

    @Override
    public String getStateOsidByName(RequestDTO requestDTO,String stateName) throws IOException {
        StatesDTO states=new StatesDTO();
        Map<String, Object> request = new HashMap<>() ;
        Map<String, Object> requestMap = new HashMap<>() ;
        Map<String, Object> requestMap1 = new HashMap<>() ;
        Map<String, Object> districtDTOList = new HashMap<>() ;
        Map<String, Object> districtDTOList2 = new HashMap<>() ;
        LoginAndRegisterResponseMap response ;

        request.put("states",stateName);
        requestMap.put("states",request);
        requestDTO.setId(Constants.ID_SEARCH_STATE);
        requestDTO.setRequest(requestMap);
        response = getStateByName(requestDTO);
        JSONObject object = new JSONObject(response);
        JSONObject subObject = object.getJSONObject("response");
        JSONObject object1 = subObject.getJSONObject("responseObject");
        JSONObject subSubObject = object1.getJSONObject("state");
        String value = (String) subSubObject.get("osid");
        return value;
    }


    @Override
    public LoginAndRegisterResponseMap getDistrictsByStateOSID(RequestDTO requestDTO) throws IOException {
        List<DistrictsDTO> districtDTOList = new ArrayList<>();
        List<DistrictsDTO> districtResponseDTOList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> responseMap = new HashMap<>();
        LoginAndRegisterResponseMap response = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = getDistricts(requestDTO, "2");
        Map<String, Object> statesMap = loginAndRegisterResponseMap.getResponse();
        if (statesMap.containsKey("districts")) {
            districtDTOList = (List<DistrictsDTO>) statesMap.get("districts");
        }
        if (!districtDTOList.isEmpty()) {
            Districts districts = mapper.convertValue(requestDTO.getRequest().get("districts"), Districts.class);
            log.info(districts.getfKeyState());
            for (int i = 0; i < districtDTOList.size(); i++) {
                if (districtDTOList.get(i).getfKeyState().toLowerCase().contains(districts.getfKeyState().toLowerCase())) {
                    districtResponseDTOList.add(districtDTOList.get(i));
                }
            }
            map.put("districts", districtResponseDTOList );
            responseMap.put("responseObject", map);
            response.setId(requestDTO.getId());
            response.setVer(requestDTO.getVer());
            response.setEts(requestDTO.getEts());
            response.setParams(requestDTO.getParams());
            response.setResponse(responseMap);
            return response;
        } else {
            log.error("empty list");
        }
        return null;
    }


    private LoginAndRegisterResponseMap generateStatesResponse(RequestDTO requestDTO,
                                                               Response<RegistryResponse> statesResponse, String flag) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        RegistryResponse registryResponse = statesResponse.body();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> statesMap = new HashMap<>();
        Map<String, Object> statesResponseMap = new HashMap<>();
        List<LinkedHashMap> statesList = (List<LinkedHashMap>) registryResponse.getResult();
        List<StatesDTO> statesDTOList = new ArrayList<>();
        statesList.stream().forEach(state -> {
            StatesDTO stateDto = new StatesDTO();
            convertStateListData(stateDto, state);
            statesDTOList.add(stateDto);
        });
        Comparator<StatesDTO> compareById = new Comparator<StatesDTO>() {
            @Override
            public int compare(StatesDTO o1, StatesDTO o2) {
                return o1.getStates().compareTo(o2.getStates());
            }
        };
        Collections.sort(statesDTOList,compareById);
        statesMap.put("states", statesDTOList);
        statesResponseMap.put("responseObject", statesMap);
        if (flag.equals("1")) {

            loginAndRegisterResponseMap.setId(requestDTO.getId());
            loginAndRegisterResponseMap.setId(requestDTO.getEts());
            loginAndRegisterResponseMap.setId(requestDTO.getVer());
            loginAndRegisterResponseMap.setParams(requestDTO.getParams());
            loginAndRegisterResponseMap.setResponse(statesResponseMap);
            return loginAndRegisterResponseMap;
        } else {
            loginAndRegisterResponseMap.setResponse(statesMap);
            return loginAndRegisterResponseMap;
        }

    }

    private LoginAndRegisterResponseMap generateDistrictsResponse(RequestDTO requestDTO,
                                                               Response<RegistryResponse> districtResponse, String flag) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        RegistryResponse registryResponse = districtResponse.body();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> districtsMap = new HashMap<>();
        Map<String, Object> districtsResponseMap = new HashMap<>();
        List<LinkedHashMap> districtsList = (List<LinkedHashMap>) registryResponse.getResult();
        List<DistrictsDTO> districtsDTOList = new ArrayList<>();

        districtsList.stream().forEach(districts -> {
            DistrictsDTO districtsDTO = new DistrictsDTO();
            convertDistrictListData(districtsDTO, districts);
            districtsDTOList.add(districtsDTO);
        });
        Comparator<DistrictsDTO> compareById = new Comparator<DistrictsDTO>() {
            @Override
            public int compare(DistrictsDTO o1, DistrictsDTO o2) {
                return o1.getDistricts().compareTo(o2.getDistricts());
            }
        };
        Collections.sort(districtsDTOList,compareById);
        districtsMap.put("districts", districtsDTOList);
        districtsResponseMap.put("responseObject", districtsMap);
        if (flag.equals("1")) {

            loginAndRegisterResponseMap.setId(requestDTO.getId());
            loginAndRegisterResponseMap.setId(requestDTO.getEts());
            loginAndRegisterResponseMap.setId(requestDTO.getVer());
            loginAndRegisterResponseMap.setParams(requestDTO.getParams());
            loginAndRegisterResponseMap.setResponse(districtsResponseMap);
            return loginAndRegisterResponseMap;
        } else {
            loginAndRegisterResponseMap.setResponse(districtsMap);
            return loginAndRegisterResponseMap;
        }

    }

    private void convertStateListData(StatesDTO stateDto, LinkedHashMap state) {
        stateDto.setStates((String) state.get("states"));
        stateDto.setOsid((String) state.get("osid"));

    }
    private void convertDistrictListData(DistrictsDTO districtsDTO, LinkedHashMap districts) {
        districtsDTO.setDistricts((String) districts.get("districts"));
        districtsDTO.setfKeyState((String) districts.get("fKeyState"));

    }





}
