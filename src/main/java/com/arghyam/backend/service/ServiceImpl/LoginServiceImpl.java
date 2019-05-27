package com.arghyam.backend.service.ServiceImpl;

import com.arghyam.backend.config.AppContext;
import com.arghyam.backend.dao.KeycloakDAO;
import com.arghyam.backend.dao.MessageService;
import com.arghyam.backend.dto.*;
import com.arghyam.backend.exceptions.BadRequestException;
import com.arghyam.backend.exceptions.UnauthorizedException;
import com.arghyam.backend.dao.KeycloakService;
import com.arghyam.backend.service.LoginService;
import com.arghyam.backend.service.UserService;
import com.arghyam.backend.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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



    ObjectMapper mapper = new ObjectMapper();

    @Override
    public LoginAndRegisterResponseMap login(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        LoginAndRegisterResponseBody response = new LoginAndRegisterResponseBody();
        if(requestDTO.getRequest().keySet().contains("person")) {
            //map person object (part of request map) to LoginDTO object
            LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
            if (loginDTO.getUsername() == null || loginDTO.getUsername().equals(null)) {
                throw new BadRequestException("Username is missing");
            } else {
                loginDTO.setPassword("password");
                AccessTokenResponseDTO accessTokenResponseDTO = userLogin(loginDTO);
                String userToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
                if (accessTokenResponseDTO !=null) {
                    response.setMessage("Otp is sent to the registered mobile number");
                    response.setNewUserCreated(false);
                } else {
                    userService.createUsers(requestDTO, userToken, bindingResult);
                    response.setMessage("Otp is sent to the registered mobile number");
                    response.setNewUserCreated(true);
                }

                UserRepresentation userRepresentation = keycloakService.getUserByUsername(userToken, loginDTO.getUsername(), appContext.getRealm());
                updateOtpForUser(loginDTO, userToken, userRepresentation);
                updateLoginResponseBody(response, loginAndRegisterResponseMap, requestDTO, "200", "Login successfull", "login");
            }
        }
        return loginAndRegisterResponseMap;
    }


    public void updateOtpForUser (LoginDTO loginDTO, String userToken, UserRepresentation userRepresentation)  throws IOException {
        if (loginDTO.getUsername().matches("[0-9]+") && userRepresentation != null) {
            String otp = generateOtp();
            List<String> otpList = new ArrayList<>();
            otpList.add(otp);
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("otp", otpList);
            messageService.sendMessage("<#> OTP for login is :" + otp + "\n" +" P9He0xQtBTT", loginDTO.getUsername());
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
                    loginDTO.getPassword(),appContext.getClientId(),appContext.getGrantType(),appContext.getClientSecret());
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
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(token,loginWithPhonenumber.getPhoneNumber(),appContext.getRealm());
        List<String> otp = new ArrayList<>();

        if(loginWithPhonenumber.getPhoneNumber().equals("+919999999999")) {
            otp.add("0123");
        } else {
            otp.add(userService.otpgenerator());
        }
        userRepresentation.getAttributes().put("otp", otp);
        keycloakService.updateUser("Bearer " + token, userRepresentation.getId(), userRepresentation, appContext.getRealm());

        try {
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public LoginAndRegisterResponseMap refreshAccessToken(RequestDTO requestDTO){
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
        if((Constants.PASSWORD.equalsIgnoreCase(loginDTO.getGrantType()) && loginDTO.getUsername() != null && loginDTO.getPassword() != null) ||
                (Constants.REFRESH_TOKEN.equalsIgnoreCase(loginDTO.getGrantType()) && loginDTO.getRefreshToken() != null)) {
            AccessTokenResponseDTO accessTokenResponseDTO = keycloakService.refreshAccessToken(loginDTO);
            if (accessTokenResponseDTO != null) {
                updateLoginResponseBody(accessTokenResponseDTO,loginAndRegisterResponseMap, requestDTO, "200", "Accesstoken generated", "refreshToken");
            } else {
                updateLoginResponseBody(accessTokenResponseDTO,loginAndRegisterResponseMap, requestDTO, "401", "Accesstoken not generated", "refreshToken");
            }
            return loginAndRegisterResponseMap;
        }  else{
            throw new UnauthorizedException("Required params are missing. Combination of {username, password, grantType=password} OR {grantType=refresh_token, refreshToken} to be provided");
        }
    }


    @Override
    public ResponseDTO logout(String id) throws IOException {
        ResponseDTO response = keycloakService.logout(id);
        return response;
    }



    public String generateOtp(){
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        return String.valueOf(randomPIN);
    }


    @Override
    public LoginAndRegisterResponseMap verifyOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        LoginAndRegisterResponseMap loginAndRegisterResponseMap = new LoginAndRegisterResponseMap();
        VerifyOtpDTO verifyOtpDTO = mapper.convertValue(requestDTO.getRequest().get("person"), VerifyOtpDTO.class);
        String token = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(token,verifyOtpDTO.getPhoneNumber(),appContext.getRealm());
        if (null!=userRepresentation && !userRepresentation.getAttributes().isEmpty()){
            List<String> otpList=userRepresentation.getAttributes().get("otp");
            String otp=otpList.get(0);
            LoginResponseDTO loginResponseDTO=keycloakService.login(userRepresentation,bindingResult);
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();
            if (otp.equals(verifyOtpDTO.getOtp())){
                accessTokenResponseDTO.setAccessToken(loginResponseDTO.getAccessToken());
                accessTokenResponseDTO.setRefreshToken(loginResponseDTO.getRefreshToken());
                userResponseDTO.setAccessTokenResponseDTO(accessTokenResponseDTO);
                updateLoginResponseBody(userResponseDTO, loginAndRegisterResponseMap, requestDTO, "200", "Otp verified", "verifyOtp");
                return loginAndRegisterResponseMap;
            }else {
                accessTokenResponseDTO.setAccessToken("");
                accessTokenResponseDTO.setRefreshToken("");
                userResponseDTO.setAccessTokenResponseDTO(accessTokenResponseDTO);
                updateLoginResponseBody(userResponseDTO, loginAndRegisterResponseMap, requestDTO, "401", "Otp not verified", "verifyOtp");
                return loginAndRegisterResponseMap;
            }
        }else {
            throw new UnauthorizedException("User doesn't exist");
        }
    }


    private void updateLoginResponseBody(Object object,
                              LoginAndRegisterResponseMap loginAndRegisterResponseMap, RequestDTO requestDTO, String responseCode, String responseStatus, String type) {
        Map<String, Object> responseMap = new HashMap<>();
        if (type.equals("verifyOtp")){
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
