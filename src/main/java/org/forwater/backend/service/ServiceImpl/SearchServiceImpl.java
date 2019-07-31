package org.forwater.backend.service.ServiceImpl;

import com.google.gson.Gson;
import org.forwater.backend.config.AppContext;
import org.forwater.backend.dao.KeycloakService;
import org.forwater.backend.dao.RegistryDAO;
import org.forwater.backend.dto.*;
import org.forwater.backend.exceptions.InternalServerException;
import org.forwater.backend.service.SearchService;
import org.forwater.backend.utils.Constants;
import org.json.JSONArray;
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
        DistrictsDTOWithoutOSID districtsDTO = new DistrictsDTOWithoutOSID();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("districts")) {
            districtsDTO = mapper.convertValue(requestDTO.getRequest().get("districts"), DistrictsDTOWithoutOSID.class);
        }
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
        return null;
    }

    @Override
    public LoginAndRegisterResponseMap postSubDistricts(RequestDTO requestDTO, String subDistrictName, String fKeyDistrict) throws IOException {
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Boolean flag = true;
        List<SubDistrictsDTO> subDistrictDTOList = new ArrayList<>();
        List<String> subDistrictsList = new ArrayList<>();
        SubDIstrictsDTOWithoutOSID subDistrictsDTO = new SubDIstrictsDTOWithoutOSID();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("subDistricts")) {
            subDistrictsDTO = mapper.convertValue(requestDTO.getRequest().get("subDistricts"), SubDIstrictsDTOWithoutOSID.class);
        }

        subDistrictsDTO.setSubDistricts(subDistrictName);
        subDistrictsDTO.setfKeyDistricts(fKeyDistrict);
        HashMap<String, Object> map = new HashMap<>();
        map.put("subDistricts", subDistrictsDTO);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        LoginAndRegisterResponseMap a = getSubDistricts(requestDTO,"2");
        Map<String, Object> districtsMap = a.getResponse();
        if (districtsMap.containsKey("subDistricts")) {
            subDistrictDTOList = (List<SubDistrictsDTO>) districtsMap.get("subDistricts");
        }
        for (int i = 0; i < subDistrictDTOList.size(); i++) {
            subDistrictsList.add(subDistrictDTOList.get(i).getSubDistricts());
        }
        for (int i = 0; i < subDistrictsList.size(); i++) {
            if (subDistrictsList.get(i).equals(subDistrictName)){
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
        return null;
    }

    @Override
    public LoginAndRegisterResponseMap postVillage(RequestDTO requestDTO, String village, String fKeySubDistrict) throws IOException {
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Boolean flag = true;
        List<VillagesDTO> villagesDTOList = new ArrayList<>();
        List<String> villagesList = new ArrayList<>();
        VillagesDTOWithoutOSID villagesDTO = new VillagesDTOWithoutOSID();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("villages")) {
            villagesDTO = mapper.convertValue(requestDTO.getRequest().get("villages"), VillagesDTOWithoutOSID.class);
        }

        villagesDTO.setVillages(village);
        villagesDTO.setfKeySubDistricts(fKeySubDistrict);
        HashMap<String, Object> map = new HashMap<>();
        map.put("villages", villagesDTO);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        LoginAndRegisterResponseMap a = getVillages(requestDTO,"2");
        Map<String, Object> districtsMap = a.getResponse();
        if (districtsMap.containsKey("villages")) {
            villagesDTOList = (List<VillagesDTO>) districtsMap.get("villages");
        }
        for (int i = 0; i < villagesDTOList.size(); i++) {
            villagesList.add(villagesDTOList.get(i).getVillages());
        }
        for (int i = 0; i < villagesList.size(); i++) {
            if (villagesList.get(i).equals(village)){
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
        return null;
    }


    @Override
    public LoginAndRegisterResponseMap postCities(RequestDTO requestDTO, String cities, String fKeySubDistrict) throws IOException {
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Boolean flag = true;
        List<CityDTO> citiesDTOList = new ArrayList<>();
        List<String> citiesList = new ArrayList<>();
        CityDTOWithoutOSID citiesDTO = new CityDTOWithoutOSID();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("cities")) {
            citiesDTO = mapper.convertValue(requestDTO.getRequest().get("cities"), CityDTOWithoutOSID.class);
        }

        citiesDTO.setCities(cities);
        citiesDTO.setfKeySubDistricts(fKeySubDistrict);
        HashMap<String, Object> map = new HashMap<>();
        map.put("cities", citiesDTO);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        LoginAndRegisterResponseMap a = getCities(requestDTO,"2");
        Map<String, Object> citiesMap = a.getResponse();
        if (citiesMap.containsKey("cities")) {
            citiesDTOList = (List<CityDTO>) citiesMap.get("cities");
        }
        for (int i = 0; i < citiesDTOList.size(); i++) {
            citiesList.add(citiesDTOList.get(i).getCities());
        }
        for (int i = 0; i < citiesList.size(); i++) {
            if (citiesList.get(i).equals(cities)){
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
    public LoginAndRegisterResponseMap getSubDistrictsOsid(RequestDTO requestDTO, String flag) throws IOException {
        retrofit2.Response<RegistryResponse> registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Map<String, String> districtsMap = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("subDistricts")) {
            districtsMap.put("@type", "subDistricts");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("subDistricts", districtsMap);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
            } else {
                return SubDistrictResponse(requestDTO, registryUserCreationResponse, flag);
            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return null;
    }

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
     * This api returns all the subDistricts available from the Db
     * @param requestDTO
     * @param flag
     * @return
     * @throws IOException
     */
    @Override
    public LoginAndRegisterResponseMap getSubDistricts(RequestDTO requestDTO, String flag) throws IOException {
        retrofit2.Response<RegistryResponse> registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Map<String, String> subDistrictsMap = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("subDistricts")) {
            subDistrictsMap.put("@type", "subDistricts");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("subDistricts", subDistrictsMap);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
            } else {
                return generateSubDistrictsResponse(requestDTO, registryUserCreationResponse, flag);
            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return null;
    }

    /**
     * This api returns all the villages available from the Db
     * @param requestDTO
     * @param flag
     * @return
     * @throws IOException
     */
    private LoginAndRegisterResponseMap getVillages(RequestDTO requestDTO, String flag) throws IOException {
        retrofit2.Response<RegistryResponse> registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Map<String, String> villagesMap = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("villages")) {
            villagesMap.put("@type", "villages");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("villages", villagesMap);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
            } else {
                return generateVillagesResponse(requestDTO, registryUserCreationResponse, flag);
            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return null;
    }

    @Override
    public LoginAndRegisterResponseMap getCities(RequestDTO requestDTO, String flag) throws IOException {
        retrofit2.Response<RegistryResponse> registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(),
                appContext.getAdminUserpassword());
        Map<String, String> citiesMap = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("cities")) {
            citiesMap.put("@type", "cities");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("cities", citiesMap);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map,
                RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
            } else {
                return generateCitiesResponse(requestDTO, registryUserCreationResponse, flag);
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
    public String getDistrictOsidByDistrictName(RequestDTO requestDTO, String district, String fKeyState) throws IOException {
        DistrictDTO states=new DistrictDTO();
        Map<String, Object> request = new HashMap<>() ;
        Map<String, Object> requestMap = new HashMap<>() ;
        LoginAndRegisterResponseMap response ;
        fKeyState = fKeyState.substring(2);
        request.put("fKeyState",fKeyState);
        requestMap.put("districts",request);
        requestDTO.setId(Constants.ID_SEARCH_STATE);
        requestDTO.setRequest(requestMap);
        response = getDistricts(requestDTO,"2");
        Gson gson = new Gson();
        String json = gson.toJson(response);
        JSONObject object = new JSONObject(json);
        JSONObject subObject = object.getJSONObject("response");
        JSONArray subSubObject = subObject.getJSONArray("districts");
        String a = "";

        for (int i = 0; i < subSubObject.length(); i++) {
            JSONObject value = subSubObject.getJSONObject(i);
            String districtname = value.getString("districts");
            if (districtname.equals(district)){
                a = (String) value.get("osid");
            }
        }
        return a;
    }

    @Override
    public String getSubDistrictOsidBySubDistrictName(RequestDTO requestDTO, String subDistrict, String fKeyDistrict) throws IOException {
        SubDistrictDTO subDistrictDTO=new SubDistrictDTO();
        Map<String, Object> request = new HashMap<>() ;
        Map<String, Object> requestMap = new HashMap<>() ;
        LoginAndRegisterResponseMap response ;
        fKeyDistrict = fKeyDistrict.substring(2);
        request.put("fKeyDistrict",fKeyDistrict);
        requestMap.put("subDistricts",request);
        requestDTO.setId(Constants.ID_SEARCH_STATE);
        requestDTO.setRequest(requestMap);
        response = getSubDistricts(requestDTO,"2");
        Gson gson = new Gson();
        String json = gson.toJson(response);
        JSONObject object = new JSONObject(json);
        JSONObject subObject = object.getJSONObject("response");
        JSONArray subSubObject = subObject.getJSONArray("subDistricts");

        String a = "";

        for (int i = 0; i < subSubObject.length(); i++) {
            JSONObject value = subSubObject.getJSONObject(i);
            String subDistrictName = value.getString("subDistricts");
            if (subDistrictName.equals(subDistrict)){
                a = (String) value.get("osid");
            }
        }
        return a;
    }


    @Override
    public String getsubDistrictOsid(RequestDTO requestDTO, String subDistrict, String fKeyDistricts) throws IOException {
        SubDistrict district=new SubDistrict();
        Map<String, Object> request = new HashMap<>() ;
        Map<String, Object> requestMap = new HashMap<>() ;
        LoginAndRegisterResponseMap response ;
        fKeyDistricts = fKeyDistricts.substring(2);
        request.put("fKeyDistricts",fKeyDistricts);
        requestMap.put("subDistricts",request);
        requestDTO.setId(Constants.ID_SEARCH_STATE);
        requestDTO.setRequest(requestMap);
        response = getSubDistrictsOsid(requestDTO,"2");
        log.info("response============"+response);
        Gson gson = new Gson();
        String json = gson.toJson(response);
        JSONObject object = new JSONObject(json);
        log.info("object=========="+object);
        JSONObject subObject = object.getJSONObject("response");
        log.info("subobject========"+subObject);
        JSONArray subSubObject = subObject.getJSONArray("subDistricts");
        log.info("subsubobject========"+subSubObject);

        String a = "";

        for (int i = 0; i < subSubObject.length(); i++) {
            JSONObject value = subSubObject.getJSONObject(i);
            String subDistrictname = value.getString("subDistricts");
            if (subDistrictname.equals(subDistrict)){
                a = (String) value.get("osid");
            }
        }
        return a;
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


    private LoginAndRegisterResponseMap SubDistrictResponse(RequestDTO requestDTO,
                                                                  Response<RegistryResponse> subDistrictResponse, String flag) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        RegistryResponse registryResponse = subDistrictResponse.body();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> subDistrictsMap = new HashMap<>();
        Map<String, Object> subDistrictsResponseMap = new HashMap<>();
        List<LinkedHashMap> subDistrictsList = (List<LinkedHashMap>) registryResponse.getResult();
        List<SubDistrictsDTO> subDistrictsDTOList = new ArrayList<>();

        subDistrictsList.stream().forEach(subDstricts -> {
            SubDistrictsDTO districtsDTO = new SubDistrictsDTO();
            convertSubDistrictsListData(districtsDTO, subDstricts);
            subDistrictsDTOList.add(districtsDTO);
        });
        Comparator<SubDistrictsDTO> compareById = new Comparator<SubDistrictsDTO>() {
            @Override
            public int compare(SubDistrictsDTO o1, SubDistrictsDTO o2) {
                return o1.getSubDistricts().compareTo(o2.getSubDistricts());
            }
        };
        Collections.sort(subDistrictsDTOList,compareById);
        subDistrictsMap.put("subDistricts", subDistrictsDTOList);
        subDistrictsResponseMap.put("responseObject", subDistrictsMap);
        if (flag.equals("1")) {

            loginAndRegisterResponseMap.setId(requestDTO.getId());
            loginAndRegisterResponseMap.setId(requestDTO.getEts());
            loginAndRegisterResponseMap.setId(requestDTO.getVer());
            loginAndRegisterResponseMap.setParams(requestDTO.getParams());
            loginAndRegisterResponseMap.setResponse(subDistrictsResponseMap);
            return loginAndRegisterResponseMap;
        } else {
            loginAndRegisterResponseMap.setResponse(subDistrictsMap);
            return loginAndRegisterResponseMap;
        }

    }

    private LoginAndRegisterResponseMap generateSubDistrictsResponse(RequestDTO requestDTO, Response<RegistryResponse> subDistrictResponse, String flag) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        RegistryResponse registryResponse = subDistrictResponse.body();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> subDistrictsMap = new HashMap<>();
        Map<String, Object> subDistrictsResponseMap = new HashMap<>();
        List<LinkedHashMap> subDistrictsList = (List<LinkedHashMap>) registryResponse.getResult();
        List<SubDistrictsDTO> subDistrictsDTOList = new ArrayList<>();

        subDistrictsList.stream().forEach(subDistricts -> {
            SubDistrictsDTO subDistrictsDTO = new SubDistrictsDTO();
            convertSubDistrictListData(subDistrictsDTO, subDistricts);
            subDistrictsDTOList.add(subDistrictsDTO);
        });
        Comparator<SubDistrictsDTO> compareById = new Comparator<SubDistrictsDTO>() {
            @Override
            public int compare(SubDistrictsDTO o1, SubDistrictsDTO o2) {
                return o1.getSubDistricts().compareTo(o2.getSubDistricts());
            }
        };
        Collections.sort(subDistrictsDTOList,compareById);
        subDistrictsMap.put("subDistricts", subDistrictsDTOList);
        subDistrictsResponseMap.put("responseObject", subDistrictsMap);
        if (flag.equals("1")) {

            loginAndRegisterResponseMap.setId(requestDTO.getId());
            loginAndRegisterResponseMap.setId(requestDTO.getEts());
            loginAndRegisterResponseMap.setId(requestDTO.getVer());
            loginAndRegisterResponseMap.setParams(requestDTO.getParams());
            loginAndRegisterResponseMap.setResponse(subDistrictsResponseMap);
            return loginAndRegisterResponseMap;
        } else {
            loginAndRegisterResponseMap.setResponse(subDistrictsMap);
            return loginAndRegisterResponseMap;
        }
    }
    private LoginAndRegisterResponseMap generateCitiesResponse(RequestDTO requestDTO, Response<RegistryResponse> citiesResponse, String flag) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        RegistryResponse registryResponse = citiesResponse.body();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> citiesMap = new HashMap<>();
        Map<String, Object> citiesResponseMap = new HashMap<>();
        List<LinkedHashMap> citiesList = (List<LinkedHashMap>) registryResponse.getResult();
        List<CityDTO> citiesDTOList = new ArrayList<>();

        citiesList.stream().forEach(cities -> {
            CityDTO citiesDTO = new CityDTO();
            convertCities(citiesDTO, cities);
            citiesDTOList.add(citiesDTO);
        });
        Comparator<CityDTO> compareById = new Comparator<CityDTO>() {
            @Override
            public int compare(CityDTO o1, CityDTO o2) {
                return o1.getCities().compareTo(o2.getCities());
            }
        };
        Collections.sort(citiesDTOList,compareById);
        citiesMap.put("cities", citiesDTOList);
        citiesResponseMap.put("responseObject", citiesMap);
        if (flag.equals("1")) {

            loginAndRegisterResponseMap.setId(requestDTO.getId());
            loginAndRegisterResponseMap.setId(requestDTO.getEts());
            loginAndRegisterResponseMap.setId(requestDTO.getVer());
            loginAndRegisterResponseMap.setParams(requestDTO.getParams());
            loginAndRegisterResponseMap.setResponse(citiesResponseMap);
            return loginAndRegisterResponseMap;
        } else {
            loginAndRegisterResponseMap.setResponse(citiesMap);
            return loginAndRegisterResponseMap;
        }
    }


    private LoginAndRegisterResponseMap generateVillagesResponse(RequestDTO requestDTO, Response<RegistryResponse> villagesResponse, String flag) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        RegistryResponse registryResponse = villagesResponse.body();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> villagesMap = new HashMap<>();
        Map<String, Object> villageResponseMap = new HashMap<>();
        List<LinkedHashMap> subDistrictsList = (List<LinkedHashMap>) registryResponse.getResult();
        List<VillagesDTO> villagesDTOList = new ArrayList<>();

        subDistrictsList.stream().forEach(subDistricts -> {
            VillagesDTO villagesDTO = new VillagesDTO();
            convertVillagesListData(villagesDTO, subDistricts);
            villagesDTOList.add(villagesDTO);
        });
        Comparator<VillagesDTO> compareById = new Comparator<VillagesDTO>() {
            @Override
            public int compare(VillagesDTO o1, VillagesDTO o2) {
                return o1.getVillages().compareTo(o2.getVillages());
            }
        };
        Collections.sort(villagesDTOList,compareById);
        villagesMap.put("villages", villagesDTOList);
        villageResponseMap.put("responseObject", villagesMap);
        if (flag.equals("1")) {

            loginAndRegisterResponseMap.setId(requestDTO.getId());
            loginAndRegisterResponseMap.setId(requestDTO.getEts());
            loginAndRegisterResponseMap.setId(requestDTO.getVer());
            loginAndRegisterResponseMap.setParams(requestDTO.getParams());
            loginAndRegisterResponseMap.setResponse(villageResponseMap);
            return loginAndRegisterResponseMap;
        } else {
            loginAndRegisterResponseMap.setResponse(villagesMap);
            return loginAndRegisterResponseMap;
        }
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



    @Override
    public LoginAndRegisterResponseMap getSubDistrictsByDistrictOSID(RequestDTO requestDTO) throws IOException {
        List<SubDistrictsDTO> subDistrictDTOList = new ArrayList<>();
        List<SubDistrictsDTO> subDistrictResponseDTOList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> responseMap = new HashMap<>();
        LoginAndRegisterResponseMap response = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = getSubDistricts(requestDTO, "2");
        Map<String, Object> districtsMap = loginAndRegisterResponseMap.getResponse();
        if (districtsMap.containsKey("subDistricts")) {
            subDistrictDTOList = (List<SubDistrictsDTO>) districtsMap.get("subDistricts");
        }
        if (!subDistrictDTOList.isEmpty()) {
            SubDistricts subDistricts = mapper.convertValue(requestDTO.getRequest().get("subDistricts"), SubDistricts.class);
            log.info(subDistricts.getfKeyDistricts());
            for (int i = 0; i < subDistrictDTOList.size(); i++) {
                if (subDistrictDTOList.get(i).getfKeyDistricts().toLowerCase().contains(subDistricts.getfKeyDistricts().toLowerCase())) {
                    subDistrictResponseDTOList.add(subDistrictDTOList.get(i));
                }
            }
            map.put("subDistricts", subDistrictResponseDTOList );
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
    public LoginAndRegisterResponseMap getVillagesBySubDistrictOSID(RequestDTO requestDTO) throws IOException {
        List<VillagesDTO> villagesDTOList = new ArrayList<>();
        List<VillagesDTO> villagesResponseDTOList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> responseMap = new HashMap<>();
        LoginAndRegisterResponseMap response = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = getVillages(requestDTO, "2");
        Map<String, Object> districtsMap = loginAndRegisterResponseMap.getResponse();
        if (districtsMap.containsKey("villages")) {
            villagesDTOList = (List<VillagesDTO>) districtsMap.get("villages");
        }
        if (!villagesDTOList.isEmpty()) {
            Villages villages = mapper.convertValue(requestDTO.getRequest().get("villages"), Villages.class);
            log.info(villages.getfKeySubDistricts());
            for (int i = 0; i < villagesDTOList.size(); i++) {
                if (villagesDTOList.get(i).getfKeySubDistricts().toLowerCase().contains(villages.getfKeySubDistricts().toLowerCase())) {
                    villagesResponseDTOList.add(villagesDTOList.get(i));
                }
            }
            map.put("villages", villagesResponseDTOList );
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
    public LoginAndRegisterResponseMap getCitiesBySubDistrictOSID(RequestDTO requestDTO) throws IOException {
        List<CityDTO> citiesDTOList = new ArrayList<>();
        List<CityDTO> citiesResponseDTOList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> responseMap = new HashMap<>();
        LoginAndRegisterResponseMap response = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = getCities(requestDTO, "2");
        Map<String, Object> statesMap = loginAndRegisterResponseMap.getResponse();
        if (statesMap.containsKey("cities")) {
            citiesDTOList = (List<CityDTO>) statesMap.get("cities");
        }
        if (!citiesDTOList.isEmpty()) {
            Cities cities = mapper.convertValue(requestDTO.getRequest().get("cities"), Cities.class);
            log.info(cities.getfKeySubDistricts());
            for (int i = 0; i < citiesDTOList.size(); i++) {
                if (citiesDTOList.get(i).getfKeySubDistricts().toLowerCase().contains(cities.getfKeySubDistricts().toLowerCase())) {
                    citiesResponseDTOList.add(citiesDTOList.get(i));
                }
            }
            map.put("cities", citiesResponseDTOList );
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

    private void convertStateListData(StatesDTO stateDto, LinkedHashMap state) {
        stateDto.setStates((String) state.get("states"));
        String statesOsid = (String)state.get("osid");
        stateDto.setOsid(statesOsid.substring(2));
    }
    private void convertDistrictListData(DistrictsDTO districtsDTO, LinkedHashMap districts) {
        districtsDTO.setDistricts((String) districts.get("districts"));
        districtsDTO.setfKeyState((String) districts.get("fKeyState"));
        String districtsOsid = (String)districts.get("osid");
        districtsDTO.setOsid( districtsOsid.substring(2));
    }
    private void convertSubDistrictListData(SubDistrictsDTO subDistrictsDTO, LinkedHashMap subDistricts){
        subDistrictsDTO.setSubDistricts((String) subDistricts.get("subDistricts"));
        subDistrictsDTO.setfKeyDistricts((String) subDistricts.get("fKeyDistricts"));
        String subDistrictOsid = (String) subDistricts.get("osid");
        subDistrictsDTO.setOsid(subDistrictOsid.substring(2));
    }
    private void convertVillagesListData(VillagesDTO villagesDTO, LinkedHashMap villages){
        villagesDTO.setVillages((String) villages.get("villages"));
        villagesDTO.setfKeySubDistricts((String) villages.get("fKeySubDistricts"));
        String villagesOsid = (String) villages.get("osid");
        villagesDTO.setOsid(villagesOsid.substring(2));
    }


    private void convertSubDistrictsListData(SubDistrictsDTO subDistrictsData, LinkedHashMap subDistricts){
        subDistrictsData.setSubDistricts((String) subDistricts.get("subDistricts"));
        subDistrictsData.setfKeyDistricts((String) subDistricts.get("fKeyDistricts"));
        String subDistrictOsid = (String) subDistricts.get("osid");
        subDistrictsData.setOsid(subDistrictOsid.substring(2));
    }

    private void convertCities( CityDTO cityDTO, LinkedHashMap cities){
        cityDTO.setCities((String) cities.get("cities"));
        cityDTO.setfKeySubDistricts((String) cities.get("fKeySubDistricts"));
        String citiesOsid = (String) cities.get("osid");
        cityDTO.setOsid(citiesOsid.substring(2));
    }
}
