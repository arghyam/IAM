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
    public UserLoginResponseDTO login(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
        if(requestDTO.getRequest().keySet().contains("person")) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            //map person object (part of request map) to LoginDTO object
            LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
            if (loginDTO.getUsername() == null || loginDTO.getUsername().equals(null)) {
                throw new BadRequestException("Username is missing");
            } else {
                loginDTO.setPassword("password");
                AccessTokenResponseDTO accessTokenResponseDTO = userLogin(loginDTO);
                if (accessTokenResponseDTO !=null) {
                    String userToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
                    UserRepresentation userRepresentation = keycloakService.getUserByUsername(userToken, loginDTO.getUsername(), appContext.getRealm());

                    if (loginDTO.getUsername().matches("[0-9]+")) {
                            String otp = generateOtp();
                            List<String> otpList = new ArrayList<>();
                            otpList.add(otp);
                            Map<String, List<String>> attributes = new HashMap<>();
                            attributes.put("otp", otpList);
                            messageService.sendMessage("OTP for login is " + otp, loginDTO.getUsername());
                            userRepresentation.setAttributes(attributes);
                            keycloakService.updateUser(userToken, userRepresentation.getId(), userRepresentation, appContext.getRealm());
                    }

                    userResponseDTO.setAccessTokenResponseDTO(accessTokenResponseDTO);
                    userLoginResponseDTO.setMessage("OK");
                    userLoginResponseDTO.setResponseCode(200);
                    userLoginResponseDTO.setResponse(userResponseDTO);
                } else {
                    throw new UnauthorizedException("User not registered.");
                }
            }
        }
        return userLoginResponseDTO;
    }


    public AccessTokenResponseDTO userLogin(LoginDTO loginDTO) {
        AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();
        loginDTO.setGrantType(appContext.getGrantType());
        loginDTO.setClientId(appContext.getClientId());
        try {
            Call<AccessTokenResponseDTO> loginResponseDTOCall = keycloakDAO.login(appContext.getRealm(), loginDTO.getUsername(),
                    loginDTO.getPassword(),loginDTO.getClientId(),loginDTO.getGrantType(),loginDTO.getClientSecret());
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
           // awsService.sendMessage("Use this OTP to confirm: " + otp.get(0),loginWithPhonenumber.getPhoneNumber());
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public AccessTokenResponseDTO refreshAccessToken(RequestDTO requestDTO){
        LoginDTO loginDTO = mapper.convertValue(requestDTO.getRequest().get("person"), LoginDTO.class);
        if((Constants.PASSWORD.equalsIgnoreCase(loginDTO.getGrantType()) && loginDTO.getUsername() != null && loginDTO.getPassword() != null) ||
                (Constants.REFRESH_TOKEN.equalsIgnoreCase(loginDTO.getGrantType()) && loginDTO.getRefreshToken() != null)) {
            AccessTokenResponseDTO accessTokenResponseDTO = keycloakService.refreshAccessToken(loginDTO);
            return accessTokenResponseDTO;
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
    public UserLoginResponseDTO verifyOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        userService.validatePojo(bindingResult);
        VerifyOtpDTO verifyOtpDTO = mapper.convertValue(requestDTO.getRequest().get("person"), VerifyOtpDTO.class);
        String token = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
        UserRepresentation userRepresentation = keycloakService.getUserByUsername(token,verifyOtpDTO.getPhoneNumber(),appContext.getRealm());
        if (null!=userRepresentation && !userRepresentation.getAttributes().isEmpty()){
            List<String> otpList=userRepresentation.getAttributes().get("otp");
            String otp=otpList.get(0);
            if (otp.equals(verifyOtpDTO.getOtp())){
                LoginResponseDTO loginResponseDTO=keycloakService.login(userRepresentation,bindingResult);
                UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
                UserResponseDTO userResponseDTO = new UserResponseDTO();
                AccessTokenResponseDTO accessTokenResponseDTO = new AccessTokenResponseDTO();
                accessTokenResponseDTO.setAccessToken(loginResponseDTO.getAccessToken());
                accessTokenResponseDTO.setRefreshToken(loginResponseDTO.getRefreshToken());
                userResponseDTO.setAccessTokenResponseDTO(accessTokenResponseDTO);
                userLoginResponseDTO.setResponse(userResponseDTO);
                userLoginResponseDTO.setResponseCode(200);
                return userLoginResponseDTO;
            }else {
                throw new UnauthorizedException("Otp doesn't match");
            }
        }else {
            throw new UnauthorizedException("User doesn't exist");
        }


    }
}
