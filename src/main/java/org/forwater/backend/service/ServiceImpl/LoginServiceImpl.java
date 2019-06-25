package org.forwater.backend.service.ServiceImpl;



import org.forwater.backend.config.AppContext;
import org.forwater.backend.dao.KeycloakDAO;
import org.forwater.backend.dao.KeycloakService;
import org.forwater.backend.dao.MessageService;
import org.forwater.backend.dao.RegistryDAO;
import org.forwater.backend.dto.*;
import org.forwater.backend.exceptions.BadRequestException;
import org.forwater.backend.exceptions.InternalServerException;
import org.forwater.backend.exceptions.UnauthorizedException;
import org.forwater.backend.service.LoginService;
import org.forwater.backend.service.UserService;
import org.forwater.backend.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.Instant;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Component
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private AppContext appContext;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private KeycloakDAO keycloakDAO;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RegistryDAO registryDAO;

    private static Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);


    ObjectMapper mapper = new ObjectMapper();

    @Override
    public LoginAndRegisterResponseMap login(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseBody response = new LoginAndRegisterResponseBody();
        if (requestDTO.getRequest().keySet().contains("person")) {
            //map person object (part of request map) to LoginDTO object
            LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
            if (loginDTO.getUsername() == null || loginDTO.getUsername().equals("")) {
                throw new BadRequestException("Username is missing");
            } else if (loginDTO.getUsername().length() < 10 || loginDTO.getUsername().length() > 10) {
                throw new BadRequestException("Username is invalid");
            } else {
                loginDTO.setPassword("password");
                AccessTokenResponseDTO accessTokenResponseDTO = userLogin(loginDTO);
                //admin user token
                String userToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
                if (accessTokenResponseDTO != null) {
                    response.setMessage("Otp is sent to the registered mobile number");
                    response.setNewUserCreated(false);
                } else {
                    userService.createUsers(requestDTO, userToken, bindingResult);
                    response.setMessage("Otp is sent to the registered mobile number");
                    response.setNewUserCreated(true);
                }
                UserRepresentation userRepresentation = keycloakService.getUserByUsername(userToken, loginDTO.getUsername(), appContext.getRealm());
                //response.setUserId(userRepresentation.getId());
                updateOtpForUser(loginDTO, userToken, userRepresentation, "login");
                updateLoginResponseBody(response, loginAndRegisterResponseMap, requestDTO, "200", "Login successfull", "login");
            }
        }
        return loginAndRegisterResponseMap;
    }


    public void updateOtpForUser(LoginDTO loginDTO, String userToken, UserRepresentation userRepresentation, String type) throws IOException {
        if (loginDTO.getUsername().matches("[0-9]+") && userRepresentation != null) {
            String otp;
            if (type.equals("login")) {
                otp = generateOtp();
            } else {
                otp = userRepresentation.getAttributes().get("otp").toString().replaceAll("\\p{P}", "");
            }
            List<String> otpList = new ArrayList<>();
            otpList.add(otp);
            List<String> createdAtList = new ArrayList<>();
            createdAtList.add(String.valueOf(Instant.now().getMillis()));
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("otp", otpList);
            attributes.put("createdAt", createdAtList);
            messageService.sendMessage("<#> OTP for login is :" + otp + "\n" + " P9He0xQtBTT", loginDTO.getUsername());
            userRepresentation.setAttributes(attributes);
            keycloakService.updateUser(userToken, userRepresentation.getId(), userRepresentation, appContext.getRealm());
        }
    }


    public AccessTokenResponseDTO userLogin(LoginDTO loginDTO) {
        AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();
        loginDTO.setGrantType(appContext.getGrantType());
        loginDTO.setClientId(appContext.getClientId());
        try {
            Call<AccessTokenResponseDTO> loginResponseDTOCall = keycloakDAO.login(appContext.getRealm(), loginDTO.getUsername(),
                    loginDTO.getPassword(), appContext.getClientId(), appContext.getGrantType(), appContext.getClientSecret());
            Response<AccessTokenResponseDTO> loginResponseDTOResponse = loginResponseDTOCall.execute();
            accessTokenResponseDTO = loginResponseDTOResponse.body();

        } catch (IOException e) {
            e.printStackTrace();
            throw new UnauthorizedException(e.getMessage());
        }
        return accessTokenResponseDTO;
    }


    @Override
    public void genarateOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        LoginWithPhonenumber loginWithPhonenumber = mapper.convertValue(requestDTO.getRequest().get("person"), LoginWithPhonenumber.class);
        String token = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(token, loginWithPhonenumber.getPhoneNumber(), appContext.getRealm());
        List<String> otp = new ArrayList<>();

        if (loginWithPhonenumber.getPhoneNumber().equals("+919999999999")) {
            otp.add("0123");
        } else {
            otp.add(userService.otpgenerator());
        }
        userRepresentation.getAttributes().put("otp", otp);
        keycloakService.updateUser("Bearer " + token, userRepresentation.getId(), userRepresentation, appContext.getRealm());

        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public LoginAndRegisterResponseMap refreshAccessToken(RequestDTO requestDTO) {
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
        if ((Constants.PASSWORD.equalsIgnoreCase(loginDTO.getGrantType()) && loginDTO.getUsername() != null && loginDTO.getPassword() != null) ||
                (Constants.REFRESH_TOKEN.equalsIgnoreCase(loginDTO.getGrantType()) && loginDTO.getRefreshToken() != null)) {
            AccessTokenResponseDTO accessTokenResponseDTO = keycloakService.refreshAccessToken(loginDTO);
            if (accessTokenResponseDTO != null) {
                updateLoginResponseBody(accessTokenResponseDTO, loginAndRegisterResponseMap, requestDTO, "200", "Accesstoken generated", "refreshToken");
            } else {
                updateLoginResponseBody(accessTokenResponseDTO, loginAndRegisterResponseMap, requestDTO, "401", "Accesstoken not generated", "refreshToken");
            }
            return loginAndRegisterResponseMap;
        } else {
            throw new UnauthorizedException("Required params are missing. Combination of {username, password, grantType=password} OR {grantType=refresh_token, refreshToken} to be provided");
        }
    }


    @Override
    public ResponseDTO logout(String id) throws IOException {
        ResponseDTO response = keycloakService.logout(id);
        return response;
    }


    public String generateOtp() {
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomPIN);
    }


    @Override
    public LoginAndRegisterResponseMap verifyOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        VerifyOtpDTO verifyOtpDTO = mapper.convertValue(requestDTO.getRequest().get("person"), VerifyOtpDTO.class);
        String token = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(token, verifyOtpDTO.getPhoneNumber(), appContext.getRealm());
        if (null != userRepresentation && !userRepresentation.getAttributes().isEmpty()) {
            List<String> otpList = userRepresentation.getAttributes().get("otp");
            String otp = otpList.get(0);
            LoginResponseDTO loginResponseDTO = keycloakService.login(userRepresentation, bindingResult);
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();
            try {
                String createdAtList = userRepresentation.getAttributes().get("createdAt").get(0);
                if (verifyOtpDTO.getOtp().length() != 4) {
                    throw new BadRequestException("Invalid Otp");
                }
                if (otp.equals(verifyOtpDTO.getOtp()) && compareTime(createdAtList)) {

                    accessTokenResponseDTO.setAccessToken(loginResponseDTO.getAccessToken());
                    accessTokenResponseDTO.setRefreshToken(loginResponseDTO.getRefreshToken());

                    userResponseDTO.setAccessTokenResponseDTO(accessTokenResponseDTO);
                    userResponseDTO.setUserId(userRepresentation.getId());
                    updateLoginResponseBody(userResponseDTO, loginAndRegisterResponseMap, requestDTO, "200", "Otp verified", "verifyOtp");
                    return loginAndRegisterResponseMap;
                } else {
                    accessTokenResponseDTO.setAccessToken("");
                    accessTokenResponseDTO.setRefreshToken("");
                    userResponseDTO.setAccessTokenResponseDTO(accessTokenResponseDTO);
                    if (compareTime(createdAtList)) {
                        updateLoginResponseBody(userResponseDTO, loginAndRegisterResponseMap, requestDTO, "401", "Otp not verified", "verifyOtp");
                    } else {
                        updateLoginResponseBody(userResponseDTO, loginAndRegisterResponseMap, requestDTO, "422", "Otp expired", "verifyOtp");
                    }

                    return loginAndRegisterResponseMap;
                }
            } catch (ParseException e) {
                System.out.println("error due to :" + e);
                e.printStackTrace();
                return null;
            }
        } else {
            throw new UnauthorizedException("User doesn't exist");
        }
    }

    @Override
    public LoginAndRegisterResponseMap myactivities(RequestDTO requestDTO) throws IOException {
        retrofit2.Response registryUserCreationResponse = null;
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        String adminToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        ActivitySearchDto activity=new ActivitySearchDto();
        if (null != requestDTO.getRequest() && requestDTO.getRequest().keySet().contains("activities")) {
            activity = mapper.convertValue(requestDTO.getRequest().get("activities"), ActivitySearchDto.class);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("activities", activity);
        String stringRequest = mapper.writeValueAsString(map);
        RegistryRequest registryRequest = new RegistryRequest(null, map, RegistryResponse.API_ID.SEARCH.getId(), stringRequest);
        try {

            Call<RegistryResponse> loginResponseDTOCall = registryDAO.searchUser(adminToken, registryRequest);
            registryUserCreationResponse = loginResponseDTOCall.execute();

            if (!registryUserCreationResponse.isSuccessful()) {
                log.info("response is un successfull due to :" + registryUserCreationResponse.errorBody().toString());
            } else {
                // successfull case
                log.info("response is successfull " + registryUserCreationResponse);
                return getActivitiesResponse(registryUserCreationResponse,requestDTO);

            }

        } catch (Exception e) {
            log.error("Error creating registry entry : {} ", e.getMessage());
            throw new InternalServerException("Internal server error");

        }


        return null;
    }

    private LoginAndRegisterResponseMap getActivitiesResponse(Response registryUserCreationResponse, RequestDTO requestDTO) {
        Map<String,Object> activitiesMap=new HashMap<>();
        LoginAndRegisterResponseMap activitiesResponse=new LoginAndRegisterResponseMap();
        activitiesResponse.setId(requestDTO.getId());
        activitiesResponse.setEts(requestDTO.getEts());
        activitiesResponse.setVer(requestDTO.getVer());
        activitiesResponse.setParams(requestDTO.getParams());
        RegistryResponse registryResponse = new RegistryResponse();
        registryResponse = (RegistryResponse) registryUserCreationResponse.body();
        BeanUtils.copyProperties(requestDTO, activitiesResponse);
        Map<String, Object> response = new HashMap<>();

        List<LinkedHashMap> activitiesList = (List<LinkedHashMap>) registryResponse.getResult();
        List<ActivitiesRequestDTO> activityData = new ArrayList<>();
        activitiesList.stream().forEach(activities -> {
            ActivitiesRequestDTO activityResponse = new ActivitiesRequestDTO();
            convertRegistryResponseToActivity(activityResponse, activities);
            activityData.add(activityResponse);
        });

        activitiesMap.put("activities",activityData);
        activitiesResponse.setResponse(activitiesMap);
        return activitiesResponse;
    }

    private void convertRegistryResponseToActivity(ActivitiesRequestDTO activityResponse, LinkedHashMap activity) {
        activityResponse.setUserId((String) activity.get("userId"));
        activityResponse.setAction((String) activity.get("action"));
        activityResponse.setCreatedAt((String) activity.get("createdAt"));
        activityResponse.setSpringName((String) activity.get("springName"));
        activityResponse.setLatitude((double) activity.get("latitude"));
        activityResponse.setLongitude((double) activity.get("longitude"));
        activityResponse.setSpringCode((String)activity.get("springCode"));
    }

    private boolean compareTime(String createdAt) throws ParseException {
        Date createdDate = new Date(Long.parseLong(createdAt));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createdDate);
        calendar.add(Calendar.MINUTE, 15);
        // cal.getTime() will give the created date + 15 minutes
        Date currentDateTime = new Date();
        if (currentDateTime.after(calendar.getTime())) {
            return false;
        } else {
            return true;
        }
    }


    private void updateLoginResponseBody(Object object,
                                         LoginAndRegisterResponseMap loginAndRegisterResponseMap, RequestDTO requestDTO,
                                         String responseCode, String responseStatus, String type) {
        Map<String, Object> responseMap = new HashMap<>();
        if (type.equals("verifyOtp")) {
            UserResponseDTO response = mapper.convertValue(object, UserResponseDTO.class);
            responseMap.put("responseObject", response);
        } else if (type.equals("refreshToken")) {
            AccessTokenResponseDTO response = mapper.convertValue(object, AccessTokenResponseDTO.class);
            responseMap.put("responseObject", response);
        } else if (type.equals("login")) {
            LoginAndRegisterResponseBody response = mapper.convertValue(object, LoginAndRegisterResponseBody.class);
            responseMap.put("responseObject", response);
        }
        BeanUtils.copyProperties(requestDTO, loginAndRegisterResponseMap);
        responseMap.put("responseCode", responseCode);
        responseMap.put("reponseStatus", responseStatus);
        loginAndRegisterResponseMap.setResponse(responseMap);
    }
}
