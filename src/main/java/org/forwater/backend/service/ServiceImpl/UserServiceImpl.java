package org.forwater.backend.service.ServiceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.forwater.backend.config.AppContext;
import org.forwater.backend.dao.KeycloakDAO;
import org.forwater.backend.dao.KeycloakService;
import org.forwater.backend.dao.RegistryDAO;

import org.forwater.backend.dto.*;
import org.forwater.backend.entity.*;
import org.forwater.backend.exceptions.InternalServerException;
import org.forwater.backend.exceptions.UnprocessableEntitiesException;
import org.forwater.backend.exceptions.ValidationError;
import org.forwater.backend.service.UserService;
import org.forwater.backend.utils.AmazonUtils;
import org.forwater.backend.utils.Constants;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.Cacheable;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    RegistryDAO registryDAO;

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
            RealmResource realmResource = keycloak.realm(appContext.getRealm());
            UsersResource userResource = realmResource.users();
            System.out.println("total number of users : " + String.valueOf(userResource.count()));

            // Create user (requires manage-users role)
            javax.ws.rs.core.Response response = userResource.create(registerResponseDTO);
            System.out.println("Repsonse: " + response.getStatusInfo());
            System.out.println(response.getLocation());
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            System.out.printf("User created with userId: %s%n", userId);

            // Get realm role "tester" (requires view-realm role)
            RoleRepresentation arghyamUserRole = realmResource.roles()//
                    .get(Constants.ARGHYAM_USERS).toRepresentation();

            // Assign arghyam role to user
            userResource.get(userId).roles().realmLevel() //
                    .add(Arrays.asList(arghyamUserRole));

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
    public LoginAndRegisterResponseMap getUserProfile(RequestDTO requestDTO, BindingResult bindingResult) throws Exception {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        UserRepresentation userRepresentation = getUserFromKeycloak(requestDTO);
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        Map<String, Object> springUser = new HashMap<>();
        if (userRepresentation != null) {
            userProfileDTO.setFirstName(userRepresentation.getFirstName());
            userProfileDTO.setUserName(userRepresentation.getUsername());
            userProfileDTO.setRole(getUserRoleBasedOnId(userRepresentation.getId()));
            springUser.put("responseObject", userProfileDTO);
            springUser.put("responseCode", 200);
            springUser.put("responseStatus", "user profile fetched");
        } else {
            springUser.put("responseCode", 404);
            springUser.put("responseStatus", "user profile not found");
        }
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        loginAndRegisterResponseMap.setResponse(springUser);
        return loginAndRegisterResponseMap;
    }

    private List<String> getUserRoleBasedOnId(String id) throws Exception {
        List<String> roles = new ArrayList<>();
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        List<RoleRepresentation> list = keycloakService.getUsersBasedOnRoleName(id,adminToken);
        for (int i = 0; i < list.size(); i++) {
            roles.add(list.get(i).getName());
        }
        return roles;
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
     * Uploads images to amazon S3
     *
     * @param file
     * @return
     */
    @Override
    public ResponseDTO updateProfilePicture(MultipartFile file) {
        URL url = null;
        String fileName = "";
        try {
            File imageFile = AmazonUtils.convertMultiPartToFile(file);
            fileName = AmazonUtils.generateFileName(file);

            PutObjectRequest request = new PutObjectRequest(appContext.getBucketName(), Constants.ARGHYAM_S3_FOLDER_LOCATION + fileName, imageFile);
            amazonS3.putObject(request);
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);
//            url = amazonS3.generatePresignedUrl(appContext.getBucketName(), "arghyam/" + fileName, expiration);
            imageFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sendResponse(fileName);
    }

    @Override
    public LoginAndRegisterResponseMap createAdditionalInfo(String springCode, RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        AdditionalInfo additionalInfo = new AdditionalInfo();
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        if (requestDTO.getRequest().keySet().contains("additionalInfo")) {
            additionalInfo = mapper.convertValue(requestDTO.getRequest().get("additionalInfo"), AdditionalInfo.class);
        }
        log.info("user data" + additionalInfo);
        additionalInfo.setSpringCode(springCode);
        Map<String, Object> additionalInfoMap = new HashMap<>();
        additionalInfoMap.put("additionalInfo", additionalInfo);
        String stringRequest = objectMapper.writeValueAsString(additionalInfoMap);
        RegistryRequest registryRequest = new RegistryRequest(null, additionalInfoMap, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        try {
            Call<RegistryResponse> createRegistryEntryCall = registryDao.createUser(adminToken, registryRequest);
            retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();
            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("responseCode", 200);
                response.put("responseStatus", "created additional information");
                BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
                loginAndRegisterResponseMap.setResponse(response);
                generateActivitiesForAdditionalDetails(adminToken, additionalInfo);
            }

        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return loginAndRegisterResponseMap;
    }


    @Override
    public Object getSpringById(RequestDTO requestDTO) throws IOException {
        retrofit2.Response registryUserCreationResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Person springs = new Person();
        Springuser springuser = new Springuser();
        DischargeDataResponse dischargeDataResponse = new DischargeDataResponse();
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();


        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("springs")) {
            springs = mapper.convertValue(requestDTO.getRequest().get("springs"), Person.class);
            Map<String, Object> springMap = new HashMap<>();
            springMap.put("springs", springs);
            String stringRequest = mapper.writeValueAsString(springMap);
            RegistryRequest registryRequest = new RegistryRequest(null, springMap, RegistryResponse.API_ID.SEARCH.getId(), stringRequest);

            try {

                Call<RegistryResponse> createRegistryEntryCall = registryDao.findEntitybyId(adminToken, registryRequest);
                registryUserCreationResponse = createRegistryEntryCall.execute();
                if (!registryUserCreationResponse.isSuccessful()) {
                    log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());

                } else {
                    RegistryResponse registryResponse = new RegistryResponse();
                    BeanUtils.copyProperties(registryUserCreationResponse.body(), registryResponse);

                    Map<String, Object> response = new HashMap<>();
                    Springs springResponse = new Springs();
                    List<LinkedHashMap> springList = (List<LinkedHashMap>) registryResponse.getResult();
                    springList.stream().forEach(springWithdischarge -> {
                        try {
                            convertRegistryResponseToSpringDischarge(springResponse, springWithdischarge, adminToken);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


                    Map<String, Object> addtionalData = new HashMap<>();
                    addtionalData.put("springCode", springResponse.getSpringCode());
                    Map<String, Object> additionalMap = new HashMap<>();
                    additionalMap.put("additionalInfo", addtionalData);
                    String stringAdditionalDataRequest = mapper.writeValueAsString(additionalMap);
                    RegistryRequest registryRequestForAdditional = new RegistryRequest(null, additionalMap, RegistryResponse.API_ID.SEARCH.getId(), stringAdditionalDataRequest);
                    Call<RegistryResponse> createRegistryEntryCallForAdditional = registryDao.searchUser(adminToken, registryRequestForAdditional);
                    retrofit2.Response<RegistryResponse> registryUserCreationResponseForAdditional = createRegistryEntryCallForAdditional.execute();


                    getAdditionalDataWithSpringCode(registryUserCreationResponseForAdditional, springResponse, null, "updateSpringProfile");
                    getDischargeDataWithSpringCode(adminToken, springs.getSpringCode(), springResponse, registryUserCreationResponseForAdditional);
                    response.put("responseCode", 200);
                    response.put("responseStatus", "successfull");
                    response.put("responseObject", springResponse);
                    BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
                    loginAndRegisterResponseMap.setResponse(response);
                }
            } catch (Exception e) {
                log.error("Error creating registry entry : {} ", e.getMessage());
                throw new InternalServerException("Internal server error");

            }
        }
        return loginAndRegisterResponseMap;
    }


    private void getAdditionalDataWithSpringCode(Response<RegistryResponse> registryUserCreationResponseForAdditional, Springs springResponse, DischargeDataResponse dischargeDataResponse, String updateFlow) throws IOException {
        if (registryUserCreationResponseForAdditional.body() != null) {
            RegistryResponse registryResponseForAdditional = new RegistryResponse();
            BeanUtils.copyProperties(registryUserCreationResponseForAdditional.body(), registryResponseForAdditional);
            log.info("*********** ADDITIONAL DATA FOR SPRING ***************" + objectMapper.writeValueAsString(registryResponseForAdditional));
            List<LinkedHashMap> additionalInfoList = (List<LinkedHashMap>) registryResponseForAdditional.getResult();
            additionalInfoList.forEach(additionalInfo -> {
                if (updateFlow.equalsIgnoreCase("updateSpringProfile")) {
                    if (additionalInfo.get("usage").getClass().toString().equals("class java.util.ArrayList")) {
                        springResponse.setUsage((List<String>) additionalInfo.get("usage"));
                    } else if (additionalInfo.get("usage").getClass().toString().equals("class java.lang.String")) {
                        String result = (String) additionalInfo.get("usage");
                        result = new StringBuilder(result).deleteCharAt(0).toString();
                        result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
                        springResponse.setUsage(Arrays.asList(result));
                    }
                    springResponse.setNumberOfHouseholds((Integer) additionalInfo.get("numberOfHousehold"));
                } else if (updateFlow.equalsIgnoreCase("updateDischargeData")) {
                    dischargeDataResponse.setSeasonality((String) additionalInfo.get("seasonality"));
                    convertStringToList(dischargeDataResponse, additionalInfo, "months");
                }

            });

        }

    }


    private void getDischargeDataWithSpringCode(String adminToken, String springCode, Springs springResponse, Response<RegistryResponse> registryUserCreationResponseForAdditional) throws IOException {
        Map<String, Object> dischargeData = new HashMap<>();
        dischargeData.put("springCode", springCode);
        Map<String, Object> dischargeDataMap = new HashMap<>();
        dischargeDataMap.put("dischargeData", dischargeData);
        String stringDischargeDataRequest = mapper.writeValueAsString(dischargeDataMap);
        RegistryRequest registryRequestForDischarge = new RegistryRequest(null, dischargeDataMap, RegistryResponse.API_ID.SEARCH.getId(), stringDischargeDataRequest);
        Call<RegistryResponse> createRegistryEntryCallForDischargeData = registryDao.searchUser(adminToken, registryRequestForDischarge);
        retrofit2.Response<RegistryResponse> registryUserCreationResponseForDischarge = createRegistryEntryCallForDischargeData.execute();

        if (registryUserCreationResponseForDischarge.body() != null) {
            RegistryResponse registryResponseForDischarge = new RegistryResponse();
            BeanUtils.copyProperties(registryUserCreationResponseForDischarge.body(), registryResponseForDischarge);
            mapExtraInformationForDisrchargeData(adminToken, springResponse, registryResponseForDischarge, registryUserCreationResponseForAdditional);
        }
    }


    private void convertRegistryResponseToDischarge(String adminToken, Springs springResponse, DischargeDataResponse dischargeDataResponse, LinkedHashMap discharge, Response<RegistryResponse> registryUserCreationResponseForAdditional) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);
        dischargeDataResponse.setUpdatedTimeStamp((String) discharge.get("updatedTimeStamp"));
        String dateString = (String) discharge.get("createdTimeStamp");
        try {
            Date date = dateFormat.parse(dateString);
            dischargeDataResponse.setCreatedTimeStamp(String.valueOf(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dischargeDataResponse.setOrgId((String) discharge.get("orgId"));
        dischargeDataResponse.setTenantId((String) discharge.get("tenantId"));
        dischargeDataResponse.setVolumeOfContainer((Double) discharge.get("volumeOfContainer"));
        dischargeDataResponse.setUserId((String) discharge.get("userId"));
        String userId = (String) discharge.get("userId");
        dischargeDataResponse.setSpringCode((String) discharge.get("springCode"));
        dischargeDataResponse.setStatus((String) discharge.get("status"));
        dischargeDataResponse.setOsid((String) discharge.get("osid"));

        UserRepresentation userRepresentation = keycloakService.getUserById(appContext.getRealm(), userId, adminToken);
        if (null != userRepresentation) {
            dischargeDataResponse.setSubmittedby(userRepresentation.getFirstName());
        }

        //dischargeDataResponse.setSubmittedby((String) discharge.get("submittedby"));
        //dischargeDataResponse.setSpringName((String) discharge.get("springName"));

        try {
            getAdditionalDataWithSpringCode(registryUserCreationResponseForAdditional, null, dischargeDataResponse, "updateDischargeData");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> litresPerSecond = new ArrayList<>();

        if (discharge.get("litresPerSecond").getClass().toString().equals("class java.lang.String")) {
            String result = (String) discharge.get("litresPerSecond");
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
            litresPerSecond.add(result);
        } else if (discharge.get("litresPerSecond").getClass().toString().equals("class java.util.ArrayList")) {
            litresPerSecond = (ArrayList<String>) discharge.get("litresPerSecond");
        }

        List<Double> updatedLitresPerSecond = new ArrayList<>();
        litresPerSecond.forEach(litrePerSecond -> {
            Double lps = Double.parseDouble(litrePerSecond);
            updatedLitresPerSecond.add(lps);
        });
        dischargeDataResponse.setLitresPerSecond(updatedLitresPerSecond);
        convertStringToList(dischargeDataResponse, discharge, "images");
        convertStringToList(dischargeDataResponse, discharge, "dischargeTime");
    }


    private void convertStringToList(DischargeDataResponse dischargeDataResponse, LinkedHashMap discharge, String attribute) {

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        if (discharge.get(attribute).getClass().toString().equals("class java.util.ArrayList")) {
            if (attribute.equals("dischargeTime")) {
                dischargeDataResponse.setDischargeTime((List<String>) discharge.get(attribute));
            } else if (attribute.equals("months")) {
                dischargeDataResponse.setMonths((List<String>) discharge.get(attribute));
            } else if (attribute.equals("images")) {
                List<URL> imageList = new ArrayList<>();
                List<String> imageNewList = new ArrayList<>();
                imageList = (List<URL>) discharge.get("images");
                for (int i = 0; i < imageList.size(); i++) {

                    URL url = amazonS3.
                            generatePresignedUrl(appContext.getBucketName()
                                    , "arghyam/" + imageList.get(i), expiration);
                    imageNewList.add(String.valueOf(url));
                }
                dischargeDataResponse.setImages(imageNewList);
            }

        } else if (discharge.get(attribute).getClass().toString().equals("class java.lang.String")) {
            String result = (String) discharge.get(attribute);
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();

            if (attribute.equals("dischargeTime")) {
                dischargeDataResponse.setDischargeTime(Arrays.asList(result));
            } else if (attribute.equals("months")) {
                dischargeDataResponse.setMonths(Arrays.asList(result));
            } else if (attribute.equals("images")) {
                URL url = amazonS3.
                        generatePresignedUrl(appContext.getBucketName()
                                , "arghyam/" + result, expiration);
                dischargeDataResponse.setImages(Arrays.asList(String.valueOf(url)));
            }
        }
    }


    private void convertRegistryResponseToSpringDischarge(Springs springResponse, LinkedHashMap spring, String adminToken) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);
        springResponse.setNumberOfHouseholds((Integer) spring.get("numberOfHouseholds"));
        springResponse.setUpdatedTimeStamp((String) spring.get("updatedTimeStamp"));
        springResponse.setOrgId((String) spring.get("orgId"));
        springResponse.setUserId((String) spring.get("userId"));
        String userId = (String) spring.get("userId");
        UserRepresentation userRepresentation = keycloakService.getUserById(appContext.getRealm(), userId, adminToken);
        if (null != userRepresentation) {
            springResponse.setSubmittedBy(userRepresentation.getFirstName());
        }

        String dateString = (String) spring.get("createdTimeStamp");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
            springResponse.setCreatedTimeStamp(String.valueOf(date.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        springResponse.setVillage((String) spring.get("village"));
        springResponse.setSpringCode((String) spring.get("springCode"));
        springResponse.setTenantId((String) spring.get("tenantId"));
        springResponse.setAccuracy((Double) spring.get("accuracy"));
        springResponse.setElevation((Double) spring.get("elevation"));
        springResponse.setLatitude((Double) spring.get("latitude"));

        springResponse.setLongitude((Double) spring.get("longitude"));
        springResponse.setOwnershipType((String) spring.get("ownershipType"));
        springResponse.setSpringName((String) spring.get("springName"));

        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);


        if (spring.get("usage").getClass().toString().equals("class java.util.ArrayList")) {
            springResponse.setUsage((List<String>) spring.get("usage"));
        } else if (spring.get("usage").getClass().toString().equals("class java.lang.String")) {
            String result = (String) spring.get("usage");
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
            springResponse.setUsage(Arrays.asList(result));
        }

        if (spring.get("images").getClass().toString().equals("class java.util.ArrayList")) {
            List<URL> imageList = new ArrayList<>();
            List<String> imageNewList = new ArrayList<>();
            imageList = (List<URL>) spring.get("images");
            for (int i = 0; i < imageList.size(); i++) {
                URL url = amazonS3.generatePresignedUrl(appContext.getBucketName(), "arghyam/" + imageList.get(i), expiration);
                imageNewList.add(String.valueOf(url));
            }
            springResponse.setImages(imageNewList);
        } else if (spring.get("images").getClass().toString().equals("class java.lang.String")) {
            String result = (String) spring.get("images");
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
            List<String> images = Arrays.asList(result);
            URL url = amazonS3.generatePresignedUrl(appContext.getBucketName(), "arghyam/" + result, expiration);
            springResponse.setImages(Arrays.asList(String.valueOf(url)));
        }
    }


    private void mapExtraInformationForDisrchargeData(String adminToken, Springs springResponse, RegistryResponse registryResponseForDischarge, Response<RegistryResponse> registryUserCreationResponseForAdditional) {
        Map<String, Object> dischargeMap = new HashMap<>();
        List<LinkedHashMap> dischargeDataList = (List<LinkedHashMap>) registryResponseForDischarge.getResult();
        List<DischargeData> updatedDischargeDataList = new ArrayList<>();
        dischargeDataList.stream().forEach(discharge -> {
            DischargeDataResponse dischargeDataResponse = new DischargeDataResponse();
            try {
                convertRegistryResponseToDischarge(adminToken, springResponse, dischargeDataResponse, discharge, registryUserCreationResponseForAdditional);
            } catch (IOException e) {
                e.printStackTrace();
            }
            updatedDischargeDataList.add(dischargeDataResponse);
            Collections.sort(updatedDischargeDataList, (o1, o2) -> o2.getCreatedTimeStamp().compareTo(o1.getCreatedTimeStamp()));
        });
        dischargeMap.put("dischargeData", updatedDischargeDataList);
        springResponse.setExtraInformation(dischargeMap);
    }


    private void mapExtraInformationForSpring(Springs springResponse, LinkedHashMap spring) {
        springResponse.setExtraInformation((Map<String, Object>) spring.get("extraInformation"));
    }


    @Override
    @Cacheable("springsCache")
    public LoginAndRegisterResponseMap getAllSprings(RequestDTO requestDTO, BindingResult bindingResult, Integer pageNumber) throws IOException {


        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        int startValue = 0, endValue = 0;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Map<String, String> springs = new HashMap<>();
        if (requestDTO.getRequest().keySet().contains("springs")) {
            springs.put("@type", "springs");
        }

        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("springs", springs);
        String stringRequest = objectMapper.writeValueAsString(entityMap);
        RegistryRequest registryRequest = new RegistryRequest(null, entityMap, RegistryResponse.API_ID.SEARCH.getId(), stringRequest);

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
                Springs springResponse = new Springs();
                try {
                    convertRegistryResponseToSpring(adminToken, springResponse, spring);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                springData.add(springResponse);
            });


            List<SpringsWithFormattedTime> updatedSpringList = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                    Locale.ENGLISH);
            springData.stream().forEach(spring -> {
                SpringsWithFormattedTime newSpring = new SpringsWithFormattedTime();
                try {
                    newSpring.setCreatedTimeStamp(dateFormat.parse(spring.getCreatedTimeStamp()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                newSpring.setImages(spring.getImages());
                newSpring.setExtraInformation(spring.getExtraInformation());
                newSpring.setNumberOfHouseholds(spring.getNumberOfHouseholds());
                newSpring.setOrgId(spring.getOrgId());
                newSpring.setOwnershipType(spring.getOwnershipType());
                newSpring.setSpringCode(spring.getSpringCode());
                newSpring.setSpringName(spring.getSpringName());
                newSpring.setTenantId(spring.getTenantId());
                newSpring.setUsage(spring.getUsage());
                newSpring.setUserId(spring.getUserId());
                newSpring.setVillage(spring.getVillage());
                newSpring.setAccuracy(spring.getAccuracy());
                newSpring.setElevation(spring.getElevation());
                newSpring.setLatitude(spring.getLatitude());
                newSpring.setLongitude(spring.getLongitude());
                updatedSpringList.add(newSpring);
            });

            updatedSpringList.sort(Comparator.comparing(SpringsWithFormattedTime::getCreatedTimeStamp).reversed());
//                List<Springs> updatedSprings = new ArrayList<>();
//                updatedSpringList.stream().forEach(springsWithFormattedTime -> {
//                    Springs spring1 = new Springs();
//                    spring1.setCreatedTimeStamp(springsWithFormattedTime.getCreatedTimeStamp().toString());
//                    spring1.setNumberOfHouseholds(springsWithFormattedTime.getNumberOfHouseholds());
//                    spring1.setUsage(springsWithFormattedTime.getUsage());
//                    spring1.setExtraInformation(springsWithFormattedTime.getExtraInformation());
//                    spring1.setImages(springsWithFormattedTime.getImages());
//                    spring1.setOrgId(springsWithFormattedTime.getOrgId());
//                    spring1.setOwnershipType(springsWithFormattedTime.getOwnershipType());
//                    spring1.setSpringCode(springsWithFormattedTime.getSpringCode());
//                    spring1.setSpringName(springsWithFormattedTime.getSpringName());
//                    spring1.setTenantId(springsWithFormattedTime.getTenantId());
//                    spring1.setUserId(springsWithFormattedTime.getUserId());
//                    spring1.setVillage(springsWithFormattedTime.getVillage());
//                    spring1.setAccuracy(springsWithFormattedTime.getAccuracy());
//                    spring1.setElevation(springsWithFormattedTime.getElevation());
//                    spring1.setLatitude(springsWithFormattedTime.getLatitude());
//                    spring1.setLongitude(springsWithFormattedTime.getLongitude());
//                    updatedSprings.add(spring1);
//                });

            PaginatedResponse paginatedResponse = new PaginatedResponse();
            if (pageNumber != null) {
                paginatedResponse(startValue, pageNumber, endValue, updatedSpringList, paginatedResponse);
            } else {
                paginatedResponse.setSprings(updatedSpringList);
                paginatedResponse.setTotalSprings(updatedSpringList.size());
            }
            response.put("responseObject", paginatedResponse);
            response.put("responseCode", 200);
            response.put("responseStatus", "all springs fetched successfully");
            loginAndRegisterResponseMap.setResponse(response);
        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
        }
        return loginAndRegisterResponseMap;
    }


    private void paginatedResponse(int startValue, int pageNumber, int endValue, List<SpringsWithFormattedTime> newSprings, PaginatedResponse paginatedResponse) {
        startValue = ((pageNumber - 1) * 5);
        endValue = (newSprings.size() > 5 * pageNumber) ? (startValue + 5) : newSprings.size();
        List<SpringsWithFormattedTime> springsList = new ArrayList<>();
        for (int j = startValue; j < endValue; j++) {
            springsList.add(newSprings.get(j));
        }
        paginatedResponse.setSprings(springsList);
        paginatedResponse.setTotalSprings(newSprings.size());
    }


    private void convertRegistryResponseToSpring(String adminToken, Springs springResponse, LinkedHashMap spring) throws IOException {

        springResponse.setUpdatedTimeStamp((String) spring.get("updatedTimeStamp"));
        springResponse.setOrgId((String) spring.get("orgId"));
        springResponse.setUserId((String) spring.get("userId"));
        mapExtraInformationForSpring(springResponse, spring);
        springResponse.setCreatedTimeStamp((String) spring.get("createdTimeStamp"));
        springResponse.setVillage((String) spring.get("village"));
        springResponse.setSpringCode((String) spring.get("springCode"));
        springResponse.setTenantId((String) spring.get("tenantId"));
        springResponse.setAccuracy((Double) spring.get("accuracy"));
        springResponse.setSpringName((String) spring.get("springName"));
        springResponse.setElevation((Double) spring.get("elevation"));
        springResponse.setLatitude((Double) spring.get("latitude"));
        springResponse.setLongitude((Double) spring.get("longitude"));
        springResponse.setOwnershipType((String) spring.get("ownershipType"));


        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);


        Map<String, Object> addtionalData = new HashMap<>();
        addtionalData.put("springCode", springResponse.getSpringCode());
        Map<String, Object> additionalMap = new HashMap<>();
        additionalMap.put("additionalInfo", addtionalData);
        String stringAdditionalDataRequest = mapper.writeValueAsString(additionalMap);
        RegistryRequest registryRequestForAdditional = new RegistryRequest(null, additionalMap, RegistryResponse.API_ID.SEARCH.getId(), stringAdditionalDataRequest);
        Call<RegistryResponse> createRegistryEntryCallForAdditional = registryDao.searchUser(adminToken, registryRequestForAdditional);
        retrofit2.Response<RegistryResponse> registryUserCreationResponseForAdditional = createRegistryEntryCallForAdditional.execute();


        getAdditionalDataWithSpringCode(registryUserCreationResponseForAdditional, springResponse, null, "updateSpringProfile");

        if (spring.get("images").getClass().toString().equals("class java.util.ArrayList")) {
            List<URL> imageList = new ArrayList<>();
            List<String> imageNewList = new ArrayList<>();
            imageList = (List<URL>) spring.get("images");
            for (int i = 0; i < imageList.size(); i++) {
                URL url = amazonS3.generatePresignedUrl(appContext.getBucketName(), "arghyam/" + imageList.get(i), expiration);
                imageNewList.add(String.valueOf(url));
            }
            springResponse.setImages(imageNewList);
        } else if (spring.get("images").getClass().toString().equals("class java.lang.String")) {
            String result = (String) spring.get("images");
            result = new StringBuilder(result).deleteCharAt(0).toString();
            result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
            List<String> images = Arrays.asList(result);
            URL url = amazonS3.generatePresignedUrl(appContext.getBucketName(), "arghyam/" + result, expiration);
            springResponse.setImages(Arrays.asList(String.valueOf(url)));
        }
    }

    /**
     * Image upload api response
     *
     * @param url
     * @return
     */
    private ResponseDTO sendResponse(String url) {
        ResponseDTO responseDTO = new ResponseDTO();
        HashMap<String, Object> map = new HashMap<>();
        map.put("imageUrl", url);
        ImageResponseDTO imageResponseDTO = new ImageResponseDTO();
        imageResponseDTO.setMap(map);
        responseDTO.setResponseCode(200);
        responseDTO.setMessage(Constants.IMAGE_UPLOAD_SUCCESS_MESSAGE);
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
    public LoginAndRegisterResponseMap updateUserProfile(String userId, RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
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
        RegistryRequest registryRequest = new RegistryRequest(null, personMap, RegistryResponse.API_ID.CREATE.getId(), stringRequest);

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
        response.put("responseStatus", "successfully fetched registered users");
        response.put("responseObject", getRegisteredUsers);
        loginAndRegisterResponseMap.setResponse(response);
        loginAndRegisterResponseMap.setVer("1.0");
        loginAndRegisterResponseMap.setId("forWater.user.getRegisteredUsers");
        return loginAndRegisterResponseMap;
    }


    @Override
    public LoginAndRegisterResponseMap createDischargeData(String springCode, RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        DischargeOsid dischargeOsid = null;
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        DischargeData dischargeData = mapper.convertValue(requestDTO.getRequest().get("dischargeData"), DischargeData.class);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();

        DischargeDataResponse dischargeDataResponse = new DischargeDataResponse();
        dischargeData.setSpringCode(springCode);
        dischargeData.setUserId(dischargeData.getUserId());
        dischargeData.setTenantId("tenantId1");
        dischargeData.setOrgId("Organisation1");
        dischargeData.setCreatedTimeStamp(new Date().toString());
        dischargeData.setUpdatedTimeStamp("");
        dischargeData.setMonths(dischargeData.getMonths() == null ? Arrays.asList("") : dischargeData.getMonths());
        dischargeData.setSeasonality(dischargeData.getSeasonality() == null ? "" : dischargeData.getSeasonality());


        Map<String, Object> dischargrMap = new HashMap<>();
        dischargrMap.put("dischargeData", dischargeData);
        String stringRequest = objectMapper.writeValueAsString(dischargrMap);
        RegistryRequest registryRequest = new RegistryRequest(null, dischargrMap, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
        try {
            Call<RegistryResponse> createRegistryEntryCall = registryDao.createUser(adminAccessToken, registryRequest);
            retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();

            BeanUtils.copyProperties(dischargeData, dischargeDataResponse);
            RegistryResponse registryResponse = new RegistryResponse();
            BeanUtils.copyProperties(registryUserCreationResponse.body(), registryResponse);
            dischargeOsid = mapper.convertValue(registryResponse.getResult(), DischargeOsid.class);
            dischargeDataResponse.setOsid(dischargeOsid.getDischargeData().getOsid());
            UserRepresentation usersInfo = keycloakService.getUserById(appContext.getRealm(), dischargeData.getUserId(), adminAccessToken);

            if (null != usersInfo) {
                dischargeDataResponse.setSubmittedby(usersInfo.getFirstName());
            }

            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            }

        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
        }

        generateActivityForDischargeData(adminAccessToken, dischargrMap);
        generateNotifications(Constants.NOTIFICATION_DISCHARGE,adminAccessToken, dischargrMap, dischargeOsid.getDischargeData().getOsid());

        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", 200);
        response.put("responseStatus", "created discharge data successfully");
        response.put("responseObject", dischargeDataResponse);
        loginAndRegisterResponseMap.setResponse(response);
        return loginAndRegisterResponseMap;
    }

    private void generateNotifications(String title,String adminAccessToken, Map<String, Object> dischargrMap, String osid) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        DischargeData dischargeData = (DischargeData) dischargrMap.get("dischargeData");
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setCreatedAt(System.currentTimeMillis());
        notificationDTO.setSpringCode(dischargeData.getSpringCode());
        notificationDTO.setUserId(dischargeData.getUserId());
        notificationDTO.setDischargeDataOsid(osid);
        notificationDTO.setStatus(dischargeData.getStatus());
        notificationDTO.setNotificationTitle(title+getFirstNameByUserId(dischargeData.getUserId()));

        map.put("notifications", notificationDTO);
        try {
            String stringRequest = mapper.writeValueAsString(map);
            RegistryRequest registryRequest = new RegistryRequest(null, map, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
            Call<RegistryResponse> notificationResponse = registryDAO.createUser(adminAccessToken, registryRequest);
            Response response = notificationResponse.execute();

            if (!response.isSuccessful()) {
                log.info("response is un successfull due to :" + response.errorBody().toString());
            } else {
                log.info("response is successfull " + response);
            }
        } catch (JsonProcessingException e) {
            log.error("error is :" + e);
        }
    }

    private String getFirstNameByUserId(String userId) throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        UserRepresentation userRepresentation = keycloakService.getUserById(appContext.getRealm(), userId, adminAccessToken);
        return userRepresentation.getFirstName();
    }


    private void generateActivityForDischargeData(String adminToken, Map<String, Object> dischargrMap) throws IOException {
        Springs springsDetails = new Springs();
        HashMap<String, Object> map = new HashMap<>();
        DischargeData dischargeData = (DischargeData) dischargrMap.get("dischargeData");
        ActivitiesRequestDTO activitiesRequestDTO = new ActivitiesRequestDTO();
        springsDetails = getSpringDetailsBySpringCode(dischargeData.getSpringCode());
        activitiesRequestDTO.setUserId(dischargeData.getUserId());
        activitiesRequestDTO.setAction("Discharge data added");
        activitiesRequestDTO.setCreatedAt(dischargeData.getCreatedTimeStamp().toString());
        activitiesRequestDTO.setLongitude(springsDetails.getLongitude());
        activitiesRequestDTO.setLatitude(springsDetails.getLatitude());
        activitiesRequestDTO.setSpringName(springsDetails.getSpringName());
        activitiesRequestDTO.setSpringCode(springsDetails.getSpringCode());
        map.put("activities", activitiesRequestDTO);

        try {
            String stringRequest = mapper.writeValueAsString(map);
            RegistryRequest registryRequest = new RegistryRequest(null, map, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
            Call<RegistryResponse> activitiesResponse = registryDAO.createUser(adminToken, registryRequest);
            Response response = activitiesResponse.execute();

            if (!response.isSuccessful()) {
                log.info("response is un successfull due to :" + response.errorBody().toString());
            } else {
                log.info("response is successfull " + response);
            }
        } catch (JsonProcessingException e) {
            log.error("error is :" + e);
        }

    }


    private Springs getSpringDetailsBySpringCode(String springCode) throws IOException {
        retrofit2.Response registryUserCreationResponse = null;
        retrofit2.Response dischargeDataResponse = null;
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Person springs = new Person();
        HashMap<String, Object> map = new HashMap<>();
        RequestDTO requestDTO = new RequestDTO();
        springs.setSpringCode(springCode);
        map.put("springs", springs);
        requestDTO.setRequest(map);

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

            Call<RegistryResponse> createRegistryEntryCall = registryDao.findEntitybyId(adminToken, registryRequest);
            registryUserCreationResponse = createRegistryEntryCall.execute();
            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            } else {
                RegistryResponse registryResponse = new RegistryResponse();
                BeanUtils.copyProperties(registryUserCreationResponse.body(), registryResponse);

                Map<String, Object> response = new HashMap<>();
                Springs springResponse = new Springs();
                List<LinkedHashMap> springList = (List<LinkedHashMap>) registryResponse.getResult();
                if (!springList.isEmpty()) {
                    springList.stream().forEach(springWithdischarge -> {
                        try {
                            convertRegistryResponseToSpringDischarge(springResponse, springWithdischarge, adminToken);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                return springResponse;

            }
        } catch (Exception e) {

        }
        return null;
    }


    @Override
    public LoginAndRegisterResponseMap createSpring(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        Springs springs = mapper.convertValue(requestDTO.getRequest().get("springs"), Springs.class);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        springs.setSpringCode(getAlphaNumericString(6));
        springs.setUserId(springs.getUserId());
        springs.setCreatedTimeStamp(new Date().toString());
        springs.setUpdatedTimeStamp("");
        springs.setUsage(springs.getUsage() == null ? Arrays.asList("") : springs.getUsage());
        springs.setNumberOfHouseholds(springs.getNumberOfHouseholds() == null ? 0 : springs.getNumberOfHouseholds());
        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("extraInfo", "geolocation");
        springs.setExtraInformation(extraInfo);

        Map<String, Object> springMap = new HashMap<>();
        springMap.put("springs", springs);
        String stringRequest = objectMapper.writeValueAsString(springMap);
        log.info("********create spring flow ***" + stringRequest);
        RegistryRequest registryRequest = new RegistryRequest(null, springMap, RegistryResponse.API_ID.CREATE.getId(), stringRequest);

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

        // storing activity related data into registry
        generateActivity(springMap, adminAccessToken);

        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        Map<String, Object> response = new HashMap<>();
        response.put("responseCode", 200);
        response.put("responseStatus", "created spring successfully");
        response.put("responseObject", springs);
        loginAndRegisterResponseMap.setResponse(response);
        log.info("********create spring flow ***" + objectMapper.writeValueAsString(loginAndRegisterResponseMap));
        return loginAndRegisterResponseMap;
    }


    private void generateActivity(Map<String, Object> springMap, String adminToken) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        Springs springs = (Springs) springMap.get("springs");
        ActivitiesRequestDTO activitySearchDto = new ActivitiesRequestDTO();
        activitySearchDto.setUserId(springs.getUserId());
        activitySearchDto.setCreatedAt(springs.getCreatedTimeStamp().toString());
        activitySearchDto.setSpringName(springs.getSpringName());
        activitySearchDto.setLatitude(springs.getLatitude());
        activitySearchDto.setLongitude(springs.getLongitude());
        activitySearchDto.setSpringCode(springs.getSpringCode());
        activitySearchDto.setAction("Spring added");
        map.put("activities", activitySearchDto);
        try {
            String stringRequest = mapper.writeValueAsString(map);
            RegistryRequest registryRequest = new RegistryRequest(null, map, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
            Call<RegistryResponse> activitiesResponse = registryDAO.createUser(adminToken, registryRequest);
            Response response = activitiesResponse.execute();

            if (!response.isSuccessful()) {
                log.info("response is un successfull due to :" + response.errorBody().toString());
            } else {
                // successfull case
                log.info("response is successfull " + response);

            }
        } catch (JsonProcessingException e) {
            log.error("error is :" + e);
        }
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


    @Override
    public LoginAndRegisterResponseMap getAdditionalDetailsForSpring(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        retrofit2.Response registryUserCreationResponse = null;
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        AdditionalInfo additionalInfo = mapper.convertValue(requestDTO.getRequest().get("additionalInfo"), AdditionalInfo.class);
        Map<String, Object> addAdditionalMap = new HashMap<>();
        Map<String, Object> springAttributeMap = new HashMap<>();
        springAttributeMap.put("springCode", additionalInfo.getSpringCode());
        addAdditionalMap.put("additionalInfo", springAttributeMap);
        String stringAdditionalInfoRequest = mapper.writeValueAsString(addAdditionalMap);
        RegistryRequest registryRequest = new RegistryRequest(null, addAdditionalMap, RegistryResponse.API_ID.SEARCH.getId(), stringAdditionalInfoRequest);

        try {

            Call<RegistryResponse> createRegistryEntryCall = registryDao.findEntitybyId(adminToken, registryRequest);
            registryUserCreationResponse = createRegistryEntryCall.execute();
            if (!registryUserCreationResponse.isSuccessful()) {
                log.error("Error Creating registry entry {} ", registryUserCreationResponse.errorBody().string());
            } else {
                RegistryResponse registryResponse = new RegistryResponse();
                BeanUtils.copyProperties(registryUserCreationResponse.body(), registryResponse);
                AdditionalInfo fetchedAdditionalData = new AdditionalInfo();
                List<LinkedHashMap> additionalDataList = (List<LinkedHashMap>) registryResponse.getResult();
                additionalDataList.stream().forEach(additionalData -> {
                    fetchedAdditionalData.setNumberOfHousehold((Integer) additionalData.get("numberOfHousehold"));
                    fetchedAdditionalData.setSeasonality((String) additionalData.get("seasonality"));
                    fetchedAdditionalData.setSpringCode((String) additionalData.get("springCode"));

                    if (additionalData.get("usage").getClass().toString().equals("class java.util.ArrayList")) {
                        fetchedAdditionalData.setUsage((List<String>) additionalData.get("usage"));
                    } else if (additionalData.get("usage").getClass().toString().equals("class java.lang.String")) {
                        String result = (String) additionalData.get("usage");
                        result = new StringBuilder(result).deleteCharAt(0).toString();
                        result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
                        fetchedAdditionalData.setUsage(Arrays.asList(result));
                    }


                    if (additionalData.get("months").getClass().toString().equals("class java.util.ArrayList")) {
                        fetchedAdditionalData.setMonths((List<String>) additionalData.get("months"));
                    } else if (additionalData.get("months").getClass().toString().equals("class java.lang.String")) {
                        String result = (String) additionalData.get("months");
                        result = new StringBuilder(result).deleteCharAt(0).toString();
                        result = new StringBuilder(result).deleteCharAt(result.length() - 1).toString();
                        fetchedAdditionalData.setMonths(Arrays.asList(result));
                    }
                });

                Map<String, Object> response = new HashMap<>();
                response.put("responseCode", 200);
                response.put("responseStatus", "successfull");
                response.put("responseObject", fetchedAdditionalData);
                BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
                loginAndRegisterResponseMap.setResponse(response);
            }
        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }
        return loginAndRegisterResponseMap;
    }

    private void generateActivitiesForAdditionalDetails(String adminToken, AdditionalInfo additionalInfo) throws IOException {
        // make a spring profile api call using spring code and get the resp details and save it to registry
        HashMap<String, Object> map = new HashMap<>();
        Springs springsDetails = null;
        ActivitiesRequestDTO activitiesRequestDTO = new ActivitiesRequestDTO();
        springsDetails = getSpringDetailsBySpringCode(additionalInfo.getSpringCode());
        activitiesRequestDTO.setUserId(springsDetails.getUserId());
        activitiesRequestDTO.setAction("Additional info added");
        activitiesRequestDTO.setCreatedAt(new Date().toString());
        activitiesRequestDTO.setLongitude(springsDetails.getLongitude());
        activitiesRequestDTO.setLatitude(springsDetails.getLatitude());
        activitiesRequestDTO.setSpringName(springsDetails.getSpringName());
        activitiesRequestDTO.setSpringCode(springsDetails.getSpringCode());
        map.put("activities", activitiesRequestDTO);

        try {
            String stringRequest = mapper.writeValueAsString(map);
            RegistryRequest registryRequest = new RegistryRequest(null, map, RegistryResponse.API_ID.CREATE.getId(), stringRequest);
            Call<RegistryResponse> activitiesResponse = registryDAO.createUser(adminToken, registryRequest);
            Response response = activitiesResponse.execute();

            if (!response.isSuccessful()) {
                log.info("response is un successfull due to :" + response.errorBody().toString());
            } else {
                // successfull case
                log.info("response is successfull " + response);

            }
        } catch (JsonProcessingException e) {
            log.error("error is :" + e);
        }
    }


    @Override
    public LoginAndRegisterResponseMap reviewerData(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        Reviewer dischargeData = mapper.convertValue(requestDTO.getRequest().get("Reviewer"), Reviewer.class);
        if (dischargeData.getStatus().equalsIgnoreCase("Accepted")) {
            String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
            Map<String, Object> dischargrMap = new HashMap<>();
            dischargrMap.put("dischargeData", dischargeData);

            String objectMapper = new ObjectMapper().writeValueAsString(dischargrMap);
            RegistryRequest registryRequest = new RegistryRequest(null, dischargrMap, RegistryResponse.API_ID.UPDATE.getId(), objectMapper);

            try {
                Call<RegistryResponse> createRegistryEntryCall = registryDao.updateUser(adminAccessToken, registryRequest);
                retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();

                if (registryUserCreationResponse.isSuccessful() && registryUserCreationResponse.code() == 200) {
                    BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
                    Map<String, Object> response = new HashMap<>();
                    response.put("responseCode", 200);
                    response.put("responseStatus", "Discharge data accepted");
                    loginAndRegisterResponseMap.setResponse(response);
                    updateNotificationsData(dischargeData);


                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("responseCode", registryUserCreationResponse.code());
                    response.put("responseStatus", registryUserCreationResponse.message());
                    loginAndRegisterResponseMap.setResponse(response);
                }

            } catch (IOException e) {
                log.error("Error creating registry entry : {} ", e.getMessage());
            }

            return loginAndRegisterResponseMap;

        } else if (dischargeData.getStatus().equalsIgnoreCase("Rejected")) {

            String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
            Map<String, Object> dischargeMap = new HashMap<>();
            dischargeMap.put("dischargeData", dischargeData);

            String objectMapper = new ObjectMapper().writeValueAsString(dischargeMap);
            RegistryRequest registryRequest = new RegistryRequest(null, dischargeMap, RegistryResponse.API_ID.UPDATE.getId(), objectMapper);

            try {
                Call<RegistryResponse> createRegistryEntryCall = registryDao.updateUser(adminAccessToken, registryRequest);
                retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();

                if (registryUserCreationResponse.isSuccessful() && registryUserCreationResponse.code() == 200) {
                    BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
                    Map<String, Object> response = new HashMap<>();
                    response.put("responseCode", 451);
                    response.put("responseStatus", "Discharge data Rejected");
                    loginAndRegisterResponseMap.setResponse(response);
                    updateNotificationsData(dischargeData);

                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("responseCode", registryUserCreationResponse.code());
                    response.put("responseStatus", registryUserCreationResponse.message());
                    loginAndRegisterResponseMap.setResponse(response);

                }

            } catch (IOException e) {
                log.error("Error creating registry entry : {} ", e.getMessage());
            }

        }
        return loginAndRegisterResponseMap;


    }

    private void updateNotificationsData(Reviewer dischargeData) throws IOException {
        String adminAccessToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        NotificationReviewEntity notificationReviewEntity = new NotificationReviewEntity();
        //notificationReviewEntity.setDischargeDataOsid(dischargeData.getOsid());
        notificationReviewEntity.setStatus(dischargeData.getStatus());
        notificationReviewEntity.setOsid(dischargeData.getNotificationOsid());
        Map<String, Object> dischargeMap = new HashMap<>();
        dischargeMap.put("notifications", notificationReviewEntity);

        String objectMapper = new ObjectMapper().writeValueAsString(dischargeMap);
        RegistryRequest registryRequest = new RegistryRequest(null, dischargeMap, RegistryResponse.API_ID.UPDATE.getId(), objectMapper);

        try {
            Call<RegistryResponse> createRegistryEntryCall = registryDao.updateUser(adminAccessToken, registryRequest);
            retrofit2.Response registryUserCreationResponse = createRegistryEntryCall.execute();

            if (registryUserCreationResponse.isSuccessful() && registryUserCreationResponse.code() == 200) {
                log.info("Successfully updated notifications entity:");

            } else {
                log.info("Error updating notifications entity:" + registryUserCreationResponse.errorBody().toString());
            }

        } catch (IOException e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
        }
    }

    @Override
    public LoginAndRegisterResponseMap getAllNotifications(RequestDTO requestDTO) throws IOException {
        retrofit2.Response registryUserCreationResponse = null;
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());

        HashMap<String, Object> map = new HashMap<>();
        if (requestDTO.getRequest().containsKey("notifications")) {
            map.put("@type", "notifications");
        }
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("notifications", map);
        String stringRequest = mapper.writeValueAsString(entityMap);
        RegistryRequest registryRequest = new RegistryRequest(null, entityMap, RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
                log.info("response is un successfull due to :" + registryUserCreationResponse.errorBody().toString());
            } else {
                // successfull case
                log.info("response is successfull " + registryUserCreationResponse);
                return getNotificationsResponse(registryUserCreationResponse, requestDTO);

            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }


        return null;
    }

    private LoginAndRegisterResponseMap getNotificationsResponse(Response registryUserCreationResponse, RequestDTO requestDTO) {
        Map<String, Object> activitiesMap = new HashMap<>();
        Map<String, Object> responseObjectMap = new HashMap<>();
        LoginAndRegisterResponseMap activitiesResponse = new LoginAndRegisterResponseMap();
        activitiesResponse.setId(requestDTO.getId());
        activitiesResponse.setEts(requestDTO.getEts());
        activitiesResponse.setVer(requestDTO.getVer());
        activitiesResponse.setParams(requestDTO.getParams());
        RegistryResponse registryResponse = new RegistryResponse();
        registryResponse = (RegistryResponse) registryUserCreationResponse.body();
        BeanUtils.copyProperties(requestDTO, activitiesResponse);
        Map<String, Object> response = new HashMap<>();

        List<LinkedHashMap> activitiesList = (List<LinkedHashMap>) registryResponse.getResult();
        List<NotificationDTOEntity> activityData = new ArrayList<>();
        activitiesList.stream().forEach(activities -> {
            NotificationDTOEntity activityResponse = new NotificationDTOEntity();
            convertRegistryResponseToNotifications(activityResponse, activities);
            activityData.add(activityResponse);

        });

        // sorting logic
        Collections.sort(activityData, Comparator.comparing(NotificationDTOEntity::getCreatedAt));
        //ascending order
        Collections.reverse(activityData);
        activitiesMap.put("notifications", activityData);
        responseObjectMap.put("responseObject", activitiesMap);
        responseObjectMap.put("responseCode", 200);
        responseObjectMap.put("responseStatus", "successfull");
        activitiesResponse.setResponse(responseObjectMap);
        return activitiesResponse;
    }

    private void convertRegistryResponseToNotifications(NotificationDTOEntity activityResponse, LinkedHashMap notifications) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                Locale.ENGLISH);

        String date = dateFormat.format(notifications.get("createdAt"));
        try {
            Date date1 = dateFormat.parse(date);
            activityResponse.setCreatedAt(String.valueOf(date1.getTime()));
        } catch (ParseException e) {
            System.out.println("exception0000000:" + e);
            e.printStackTrace();
        }
        activityResponse.setUserId((String) notifications.get("userId"));


        activityResponse.setSpringCode((String) notifications.get("springCode"));
        activityResponse.setDischargeDataOsid((String) notifications.get("dischargeDataOsid"));
        activityResponse.setStatus((String) notifications.get("status"));
        activityResponse.setNotificationTitle((String) notifications.get("notificationTitle"));
        activityResponse.setOsid((String) notifications.get("osid"));
    }


}
