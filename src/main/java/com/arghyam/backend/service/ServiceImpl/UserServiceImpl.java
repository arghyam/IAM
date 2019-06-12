package com.arghyam.backend.service.ServiceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.arghyam.backend.config.AppContext;
import com.arghyam.backend.dao.KeycloakDAO;
import com.arghyam.backend.dao.KeycloakService;
import com.arghyam.backend.dao.RegistryDAO;
import com.arghyam.backend.dto.*;
import com.arghyam.backend.entity.*;
import com.arghyam.backend.exceptions.InternalServerException;
import com.arghyam.backend.exceptions.BadRequestException;
import com.arghyam.backend.exceptions.UnauthorizedException;
import com.arghyam.backend.exceptions.UnprocessableEntitiesException;
import com.arghyam.backend.exceptions.ValidationError;
import com.arghyam.backend.service.UserService;
import com.arghyam.backend.utils.AmazonUtils;
import com.arghyam.backend.utils.Constants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static com.arghyam.backend.utils.Constants.ARGHYAM_S3_FOLDER_LOCATION;
import static com.arghyam.backend.utils.Constants.IMAGE_UPLOAD_SUCCESS_MESSAGE;
import static java.util.Arrays.asList;

@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    AppContext appContext;


    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KeycloakDAO keycloakDAO;

    @Autowired
    Keycloak keycloak;

    @Autowired
    KeycloakService keycloakService;


    @Autowired
    RegistryDAO registryDao;

    @Autowired
    LoginServiceImpl loginServiceImpl;

    ObjectMapper mapper = new ObjectMapper();

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    @Override
    public void createUsers(RequestDTO requestDTO, String userToken, BindingResult bindingResult) throws IOException {
        validatePojo(bindingResult);
        UserRepresentation registerResponseDTO = mapper.convertValue(requestDTO.getRequest().get("person"), UserRepresentation.class);
        try {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue("password");
            credential.setTemporary(false);
            registerResponseDTO.setEnabled(Boolean.TRUE);      // A disabled user cannot login.
            registerResponseDTO.setCredentials(asList(credential));
            keycloakService.register(userToken, registerResponseDTO);
        } catch (Exception e) {
            System.out.println("exception" + e);
        }
    }


    public CredentialRepresentation setCredentialPassword(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

    @Override
    public Keycloak getKeycloak() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(appContext.getKeyCloakServiceUrl())
                .realm(appContext.getRealm())
                .username(appContext.getAdminUserName())
                .password(appContext.getAdminUserpassword())
                .clientId(appContext.getClientId())
                .clientSecret(appContext.getClientSecret())
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();

        return keycloak;
    }


    @Override
    public void validatePojo(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<ValidationError> errorList = new ArrayList<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                ValidationError validationError = new ValidationError(error.getField(), error.getDefaultMessage());
                errorList.add(validationError);
            }
            throw new UnprocessableEntitiesException(errorList);
        }
    }


    @Override
    public String otpgenerator() {
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomPIN);
    }

    @Override
    public LoginAndRegisterResponseMap getUserProfile(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        UserRepresentation userRepresentation = getUserFromKeycloak(requestDTO);
        Map<String, Object> springUser = new HashMap<>();
        if (userRepresentation != null) {
            springUser.put("responseObject", userRepresentation);
            springUser.put("responseCode", 200);
            springUser.put("responseStatus", "user profile fetched");
        } else {
            springUser.put("responseObject", userRepresentation);
            springUser.put("responseCode", 404);
            springUser.put("responseStatus", "user profile not found");
        }
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        loginAndRegisterResponseMap.setResponse(springUser);
        return loginAndRegisterResponseMap;
    }


    @Override
    public LoginAndRegisterResponseMap reSendOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        UserRepresentation userRepresentation = null;
        validatePojo(bindingResult);
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("person")) {
            LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
            userRepresentation = keycloakService.getUserByUsername(adminToken, loginDTO.getUsername(), appContext.getRealm());
            loginServiceImpl.updateOtpForUser(loginDTO, adminToken, userRepresentation, "resendOtp");
        }
        LoginAndRegisterResponseMap responseDTO = new LoginAndRegisterResponseMap();
        responseDTO.setId(requestDTO.getId());
        responseDTO.setEts(requestDTO.getEts());
        responseDTO.setVer(requestDTO.getVer());
        responseDTO.
                setParams(requestDTO.getParams());
        HashMap<String, Object> map = new HashMap<>();
        map.put("responseCode", 200);
        map.put("responseStatus", "Otp sent successfully");
        map.put("response", null);

        responseDTO.setResponse(map);
        return responseDTO;
    }

    /**
     * Upload's images to amazon S3
     *
     * @param file
     * @return
     */
    @Override
    public ResponseDTO updateProfilePicture(MultipartFile file) {
        URL url = null;
        try {
            File imageFile = AmazonUtils.convertMultiPartToFile(file);
            String fileName = AmazonUtils.generateFileName(file);

            PutObjectRequest request = new PutObjectRequest(appContext.getBucketName(), ARGHYAM_S3_FOLDER_LOCATION + fileName, imageFile);
            amazonS3.putObject(request);
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);
            url = amazonS3.generatePresignedUrl(appContext.getBucketName(), "arghyam/" + fileName, expiration);
            imageFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sendResponse(url);
    }

    @Override
    public LoginAndRegisterResponseMap createAdditionalInfo(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        AdditionalInfo additionalInfo = new AdditionalInfo();
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        if (requestDTO.getRequest().keySet().contains("additionalInfo")) {
            additionalInfo = mapper.convertValue(requestDTO.getRequest().get("additionalInfo"), AdditionalInfo.class);
        }
        log.info("user data" + additionalInfo);
        Map<String, Object> additionalInfoMap = new HashMap<>();
        additionalInfoMap.put("additionalInfo", additionalInfo);
        String stringRequest = objectMapper.writeValueAsString(additionalInfoMap);
        RegistryRequest registryRequest = new RegistryRequest(null, additionalInfoMap, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        if (additionalInfo.getWaterUseList().isEmpty()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("responseCode", 422);
            map.put("responseStatus", "unProcessable entity");
            loginAndRegisterResponseMap.setResponse(map);
            return loginAndRegisterResponseMap;
            //error response call
        } else {
            // retrofit call
            try {
                Call<RegistryResponse> createRegistryEntryCall = registryDao.createUser(adminToken, registryRequest);
                retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();
                if (!registryUserCreationResponse.isSuccessful()) {
                    log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("responseCode", 200);
                    response.put("responseStatus", "created additional information");
                    //response.put("responseObject", registryRequest);
                    loginAndRegisterResponseMap.setResponse(response);
                }

            } catch (IOException e) {
                log.error("Error creating registry entry : {} ", e.getMessage());
                throw new InternalServerException("Internal server error");

            }

            return loginAndRegisterResponseMap;
        }
    }

    @Override
    public Object getSpringById(RequestDTO requestDTO) throws IOException {
        retrofit2.Response registryUserCreationResponse = null;
        retrofit2.Response dischargeDataResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Person springs = new Person();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("springs")) {
            springs = mapper.convertValue(requestDTO.getRequest().get("springs"), Person.class);
        }
        Map<String, Object> springMap = new HashMap<>();
        springMap.clear();
        if (springMap.isEmpty()) {
            springMap.put("springs", springs);
        } else {
            springMap.clear();
            springMap.put("springs", springs);
        }
        String stringRequest = mapper.writeValueAsString(springMap);
        RegistryRequest registryRequest = new RegistryRequest(null, springMap, RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> createRegistryEntryCall = registryDao.findSpringbyId(adminToken, registryRequest);
            registryUserCreationResponse = createRegistryEntryCall.execute();
            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            } else {
                RegistryResponse registryResponse = new RegistryResponse();
                BeanUtils.copyProperties(registryUserCreationResponse.body(), registryResponse);

                Map<String, Object> response = new HashMap<>();
                Springs springResponse = new Springs();
                getDischargeDataForASpring(adminToken, springs.getSpringCode(), registryResponse, springResponse);
                response.put("responseCode", 200);
                response.put("responseStatus", "successfull");
                response.put("responseObject", springResponse);
                loginAndRegisterResponseMap.setId(requestDTO.getId());
                loginAndRegisterResponseMap.setEts(requestDTO.getEts());
                loginAndRegisterResponseMap.setVer(requestDTO.getVer());
                loginAndRegisterResponseMap.setParams(requestDTO.getParams());
                loginAndRegisterResponseMap.setResponse(response);
            }
        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return loginAndRegisterResponseMap;
    }



    private void getDischargeDataForASpring(String adminToken, String springCode, RegistryResponse registryResponse, Springs springResponse) throws IOException {
        Map<String, Object> dischargeData = new HashMap<>();
        dischargeData.put("springCode", springCode);
        Map<String, Object> dischargeDataMap = new HashMap<>();
        dischargeDataMap.put("dischargeData", dischargeData);
        String stringDischargeDataRequest = mapper.writeValueAsString(dischargeDataMap);
        RegistryRequest registryRequestForDischarge = new RegistryRequest(null, dischargeDataMap, RegistryResponse.API_ID.SEARCH.getId(), stringDischargeDataRequest);
        Call<RegistryResponse> createRegistryEntryCallForDischargeData = registryDao.searchUser(adminToken, registryRequestForDischarge);
        retrofit2.Response<RegistryResponse>  registryUserCreationResponseForDischarge = createRegistryEntryCallForDischargeData.execute();

        RegistryResponse registryResponseForDischarge = new RegistryResponse();
        BeanUtils.copyProperties(registryUserCreationResponseForDischarge.body(), registryResponseForDischarge);
        log.info(" ************** SPRING DISCHARGE DATA **********" + objectMapper.writeValueAsString(registryResponseForDischarge));
        List<LinkedHashMap> springList = (List<LinkedHashMap>) registryResponse.getResult();
        springList.stream().forEach(springWithdischarge -> {
            convertRegistryResponseToSpringDischarge (springResponse, springWithdischarge);
            mapExtraInformationForDisrchargeData (springResponse, registryResponseForDischarge);
        });
    }




    private void convertRegistryResponseToSpringDischarge (Springs springResponse, LinkedHashMap spring) {
        springResponse.setNumberOfHouseholds((Integer) spring.get("numberOfHouseholds"));
        springResponse.setUsage((String) spring.get("usage"));
        springResponse.setUpdatedTimeStamp((String) spring.get("updatedTimeStamp"));
        springResponse.setOrgId((String) spring.get("orgId"));
        springResponse.setUserId((String) spring.get("userId"));
        springResponse.setCreatedTimeStamp((String) spring.get("createdTimeStamp"));
        springResponse.setVillage((String) spring.get("village"));
        springResponse.setSpringCode((String) spring.get("springCode"));
        springResponse.setTenantId((String) spring.get("tenantId"));
        springResponse.setAccuracy((Double) spring.get("accuracy"));
        springResponse.setElevation((Double) spring.get("elevation"));
        springResponse.setLatitude((Double) spring.get("latitude"));
        springResponse.setLongitude((Double) spring.get("longitude"));
        springResponse.setOwnershipType((String) spring.get("ownershipType"));

        log.info("***************** IMAGE OBJECT TYPE " + spring.get("images").getClass());
        if (spring.get("images").getClass().toString().equals("class java.util.ArrayList")) {
            springResponse.setImages((List<String>) spring.get("images"));
        } else if (spring.get("images").getClass().toString().equals("class java.lang.String")){
            String result = (String) spring.get("images");
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length()-1).toString();
            List<String> images = Arrays.asList(result);
            springResponse.setImages(images);
        }
    }


    private void mapExtraInformationForDisrchargeData (Springs springResponse, RegistryResponse registryResponseForDischarge) {
        Map<String, Object> dischargeMap = new HashMap<>();
        dischargeMap.put("dischargeData", registryResponseForDischarge.getResult());
        springResponse.setExtraInformation(dischargeMap);
    }


    private void mapExtraInformationForSpring(Springs springResponse, LinkedHashMap spring) {
        springResponse.setExtraInformation((Map<String, Object>) spring.get("extraInformation"));
    }



    @Override
    public LoginAndRegisterResponseMap getAllSprings(RequestDTO requestDTO, BindingResult bindingResult, Integer pageNumber) throws IOException {

        if (pageNumber == null) {
            throw new BadRequestException("pageNumber is invalid");
        } else if (pageNumber <= 0) {
            throw new UnauthorizedException("pageNumber is invalid");
        } else {
            LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
            int startValue=0, endValue=0;
            String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
            Map<String, String> springs = new HashMap<>();
            if (requestDTO.getRequest().keySet().contains("springs")) {
                springs.put("@type", "springs");
            }

            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("springs", springs);
            String stringRequest = objectMapper.writeValueAsString(entityMap);
            RegistryRequest registryRequest=new RegistryRequest(null,entityMap, RegistryResponse.API_ID.SEARCH.getId(),stringRequest);

            try {
                Call<RegistryResponse> createRegistryEntryCall = registryDao.searchUser(adminToken, registryRequest);
                retrofit2.Response<RegistryResponse> registryUserCreationResponse = createRegistryEntryCall.execute();
                if (!registryUserCreationResponse.isSuccessful()) {
                    log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
                }

                RegistryResponse registryResponse = new RegistryResponse();
                registryResponse = registryUserCreationResponse.body();
                BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
                Map<String, Object> response = new HashMap<>();
                List<LinkedHashMap> springList = (List<LinkedHashMap>) registryResponse.getResult();
                List<Springs> springData = new ArrayList<>();
                springList.stream().forEach(spring -> {
                    int i = 0;
                    Springs springResponse = new Springs();
                    convertRegistryResponseToSpring (springResponse, spring);
                    springData.add(springResponse);
                });

                Set<Springs> springSet = springData.stream().collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Springs::getCreatedTimeStamp).reversed())));
                List<Springs> newSprings = new ArrayList<>();
                newSprings.addAll(springSet);

                PaginatedResponse paginatedResponse = new PaginatedResponse();
                paginatedResponse(startValue, pageNumber, endValue, newSprings, paginatedResponse);
                response.put("responseObject", paginatedResponse);
                response.put("responseCode", 200);
                response.put("responseStatus", "all springs fetched successfully");
                loginAndRegisterResponseMap.setResponse(response);
            } catch (IOException e) {
                log.error("Error creating registry entry : {} ", e.getMessage());
            }

            return loginAndRegisterResponseMap;
        }
    }


    private void paginatedResponse (int startValue, int pageNumber, int endValue, List<Springs> newSprings, PaginatedResponse paginatedResponse) {
        startValue = ((pageNumber - 1) * 5);
        endValue = (startValue + 5);
        List<Springs> springsList = new ArrayList<>();
        for (int j=startValue; j<endValue; j++) {
            springsList.add(newSprings.get(j));
        }
        paginatedResponse.setSprings(springsList);
        paginatedResponse.setTotalSprings(newSprings.size());
    }


    private void convertRegistryResponseToSpring (Springs springResponse, LinkedHashMap spring) {

        springResponse.setNumberOfHouseholds((Integer) spring.get("numberOfHouseholds"));
        springResponse.setUsage((String) spring.get("usage"));
        springResponse.setUpdatedTimeStamp((String) spring.get("updatedTimeStamp"));
        springResponse.setOrgId((String) spring.get("orgId"));
        springResponse.setUserId((String) spring.get("userId"));
        mapExtraInformationForSpring(springResponse, spring);
        springResponse.setCreatedTimeStamp((String) spring.get("createdTimeStamp"));
        springResponse.setVillage((String) spring.get("village"));
        springResponse.setSpringCode((String) spring.get("springCode"));
        springResponse.setTenantId((String) spring.get("tenantId"));
        springResponse.setAccuracy((Double) spring.get("accuracy"));
        springResponse.setElevation((Double) spring.get("elevation"));
        springResponse.setLatitude((Double) spring.get("latitude"));
        springResponse.setLongitude((Double) spring.get("longitude"));
        springResponse.setOwnershipType((String) spring.get("ownershipType"));

        log.info("***************** IMAGE OBJECT TYPE " + spring.get("images").getClass());
        if (spring.get("images").getClass().toString().equals("class java.util.ArrayList")) {
            springResponse.setImages((List<String>) spring.get("images"));
        } else if (spring.get("images").getClass().toString().equals("class java.lang.String")){
            String result = (String) spring.get("images");
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length()-1).toString();
            List<String> images = Arrays.asList(result);
            springResponse.setImages(images);
        }
    }



    /**
     * Image upload api response
     *
     * @param url
     * @return
     */
    private ResponseDTO sendResponse(URL url) {
        ResponseDTO responseDTO = new ResponseDTO();
        HashMap<String, Object> map = new HashMap<>();
        map.put("imageUrl", url);
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
        imageResponseDTO.setMap(map);
        responseDTO.setResponseCode(200);
        responseDTO.setMessage(IMAGE_UPLOAD_SUCCESS_MESSAGE);
        responseDTO.setResponse(map);
        return responseDTO;
    }


    private UserRepresentation getUserFromKeycloak(RequestDTO requestDTO) throws IOException {
        String userToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Springuser springuser = new Springuser();
        if (requestDTO.getRequest().keySet().contains("person")) {
            springuser = mapper.convertValue(requestDTO.getRequest().get("person"), Springuser.class);
        }
        return keycloakService.getUserByUsername(userToken, springuser.getPhonenumber(), appContext.getRealm());
    }


    @Override
    public LoginAndRegisterResponseMap updateUserProfile(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        validatePojo(bindingResult);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        String userToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Springuser springuser = new Springuser();
        if (requestDTO.getRequest().keySet().contains("person")) {
            springuser = mapper.convertValue(requestDTO.getRequest().get("person"), Springuser.class);
        }

        UserRepresentation userRepresentation = keycloakService.getUserByUsername(userToken, springuser.getPhonenumber(), appContext.getRealm());
        if (userRepresentation != null) {
            userRepresentation.setFirstName(springuser.getName());
        }
        keycloakService.updateUser(userToken, userRepresentation.getId(), userRepresentation, appContext.getRealm());
        Map<String, Object> springUser = new HashMap<>();
        springUser.put("responseObject", null);
        springUser.put("responseCode", 200);
        springUser.put("responseStatus", "user profile updated");
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        loginAndRegisterResponseMap.setResponse(springUser);
        return loginAndRegisterResponseMap;
    }


    @Override
    public LoginAndRegisterResponseMap createRegistryUser(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Springuser springuser = new Springuser();
        if (requestDTO.getRequest().keySet().contains("person")) {
            springuser = mapper.convertValue(requestDTO.getRequest().get("person"), Springuser.class);
        }
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(adminAccessToken, springuser.getPhonenumber(), appContext.getRealm());
        RegistryUser person = new RegistryUser(springuser.getName(), "", "",
                "", "", new java.util.Date().toString(), new java.util.Date().toString(), springuser.getPhonenumber());


        Map<String, Object> personMap = new HashMap<>();
        personMap.put("person", person);
        String stringRequest = objectMapper.writeValueAsString(personMap);
        RegistryRequest registryRequest = new RegistryRequest(null, personMap, com.arghyam.backend.dto.RegistryResponse.API_ID.CREATE.getId(), stringRequest);

        try {
            Call<RegistryResponse> createRegistryEntryCall = registryDao.createUser(adminAccessToken, registryRequest);
            retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();
            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            }

            userRepresentation.getAttributes().put(Constants.REG_ENTRY_CREATED, asList(Boolean.TRUE.toString()));
            retrofit2.Response updateKeycloakUser = keycloakDAO.updateUser("Bearer" + adminAccessToken, userRepresentation.getId(), userRepresentation, appContext.getRealm()).execute();
            if (!updateKeycloakUser.isSuccessful()) {
                log.error("Error Updating user {} ", updateKeycloakUser.errorBody().string());
            }
            log.info("Registry entry created and user is successfully logged in");

        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
        }
        return null;
    }


    @Override
    public LoginAndRegisterResponseMap getRegistereUsers() throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        List<UserRepresentation> getRegisteredUsers = keycloakService.getRegisteredUsers(adminAccessToken, appContext.getRealm());
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", 200);
        response.put("responseStatus", "Fetched list of registered users");
        response.put("responseObject", getRegisteredUsers);
        loginAndRegisterResponseMap.setResponse(response);
        loginAndRegisterResponseMap.setVer("1.0");
        loginAndRegisterResponseMap.setId("forWater.user.getRegisteredUsers");
        return loginAndRegisterResponseMap;
    }

    @Override
    public LoginAndRegisterResponseMap createDischargeData(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        DischargeData dischargeData = mapper.convertValue(requestDTO.getRequest().get("dischargeData"), DischargeData.class);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();

        DischargeData discharge = new DischargeData();
        BeanUtils.copyProperties(dischargeData, discharge);
        discharge.setUserId(UUID.randomUUID().toString());
        discharge.setTenantId("tenantId1");
        discharge.setOrgId("Organisation1");
        discharge.setCreatedTimeStamp(new Date().toString());
        discharge.setUpdatedTimeStamp("");
        discharge.setSeasonality("Sessional");
        discharge.setMonths(Arrays.asList("January"));

        Map<String, Object> dischargrMap = new HashMap<>();
        dischargrMap.put("dischargeData", discharge);
        String stringRequest = objectMapper.writeValueAsString(dischargrMap);
        RegistryRequest registryRequest = new RegistryRequest(null, dischargrMap, com.arghyam.backend.dto.RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        try {
            Call<RegistryResponse> createRegistryEntryCall = registryDao.createUser(adminAccessToken, registryRequest);
            retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();
            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            }

        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
        }

        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", 200);
        response.put("responseStatus", "created discharge data successfully");
        response.put("responseObject", discharge);
        loginAndRegisterResponseMap.setResponse(response);
        return loginAndRegisterResponseMap;
    }

    @Override
    public LoginAndRegisterResponseMap createSpring(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Springs springs = mapper.convertValue(requestDTO.getRequest().get("springs"), Springs.class);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();

        Springs springDto = new Springs();
        BeanUtils.copyProperties(springs, springDto);
        springDto.setSpringCode(getAlphaNumericString(6));
        springDto.setUserId(UUID.randomUUID().toString());
        springDto.setCreatedTimeStamp(new Date().toString());
        springDto.setUpdatedTimeStamp("");
        springDto.setUsage("irrigation");
        springDto.setNumberOfHouseholds(2);
        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("extraInfo", "geolocation");
        springDto.setExtraInformation(extraInfo);

        log.info("********create spring flow ***" + springDto);

        Map<String, Object> springMap = new HashMap<>();
        springMap.put("springs", springDto);
        String stringRequest = objectMapper.writeValueAsString(springMap);
        log.info("********create spring flow ***" + stringRequest);
        RegistryRequest registryRequest = new RegistryRequest(null, springMap, com.arghyam.backend.dto.RegistryResponse.API_ID.CREATE.getId(), stringRequest);

        log.info("********create spring flow ***" + objectMapper.writeValueAsString(registryRequest));
        try {
            Call<RegistryResponse> createRegistryEntryCall = registryDao.createUser(adminAccessToken, registryRequest);
            retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            }

        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
        }

        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", 200);
        response.put("responseStatus", "created discharge data successfully");
        response.put("responseObject", springDto);
        loginAndRegisterResponseMap.setResponse(response);
        log.info("********create spring flow ***" + objectMapper.writeValueAsString(loginAndRegisterResponseMap));
        return loginAndRegisterResponseMap;
    }


    public static String getAlphaNumericString(int n) {
        byte[] array = new byte[256];
        new Random().nextBytes(array);

        String randomString
                = new String(array, Charset.forName("UTF-8"));
        StringBuffer r = new StringBuffer();

        String AlphaNumericString
                = randomString
                .replaceAll("[^A-Za-z0-9]", "");

        for (int k = 0; k < AlphaNumericString.length(); k++) {
            if (Character.isLetter(AlphaNumericString.charAt(k))
                    && (n > 0)
                    || Character.isDigit(AlphaNumericString.charAt(k))
                    && (n > 0)) {
                r.append(AlphaNumericString.charAt(k));
                n--;
            }
        }
        return r.toString();
    }

}


