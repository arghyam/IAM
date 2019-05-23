package com.arghyam.backend.controller;

import com.arghyam.backend.config.AppContext;
import com.arghyam.backend.dto.*;
import com.arghyam.backend.entity.Springuser;
import com.arghyam.backend.exceptions.UserCreateException;
import com.arghyam.backend.service.LoginService;
import com.arghyam.backend.service.UserService;
import com.arghyam.backend.utils.Constants;
import com.arghyam.backend.utils.KeycloakUtil;
import org.keycloak.common.VerificationException;
import org.keycloak.exceptions.TokenNotActiveException;
import org.keycloak.exceptions.TokenSignatureInvalidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpUtils;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(value = "api/v1", produces = {"application/json", "application/x-www-form-urlencoded"}, consumes = {"application/json", "application/x-www-form-urlencoded"})
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    AppContext appContext;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public LoginAndRegisterResponseMap login(@Validated @RequestBody RequestDTO requestDTO,
                        BindingResult bindingResult) throws IOException {
        return loginService.login(requestDTO, bindingResult);
    }



    @RequestMapping(value = "/user/generate-accesstoken", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public LoginAndRegisterResponseMap generateAccessToken(@RequestBody RequestDTO requestDTO) throws Exception {
            return loginService.refreshAccessToken(requestDTO);
    }



    @RequestMapping(value = "/user/verifyOtp", method = RequestMethod.POST)
    LoginAndRegisterResponseMap verifyOtp(@Validated @RequestBody RequestDTO requestDTO, BindingResult bindingResult)throws IOException {

        return loginService.verifyOtp(requestDTO,bindingResult);
    }



    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/logout", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseDTO logout(@RequestHeader("access-token") String accessToken) throws IOException, VerificationException {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            String userId = KeycloakUtil.fetchUserIdFromToken(accessToken, appContext.getKeyCloakServiceUrl(), appContext.getRealm());
            responseDTO = loginService.logout(userId);

        } catch (Exception exception) {
            responseDTO = handleAccessTokenException(exception);
        }
        return responseDTO;

    }


    public static ResponseDTO handleAccessTokenException(Exception accessTokenException) {
        ResponseDTO responseDTO = new ResponseDTO();

        if (accessTokenException instanceof TokenSignatureInvalidException) {
            responseDTO.setMessage("Signature of  access token is improper. Missed some content of Access Token ");
            responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
        } else if (accessTokenException instanceof TokenNotActiveException) {
            responseDTO.setMessage("Inactive access token. Please try with new  access token ");
            responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
        } else if (accessTokenException instanceof UserCreateException) {
            responseDTO.setMessage("User not found with this EmailId.");
            responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
        } else {
            responseDTO.setMessage("Issue with Access token, please try again");
            responseDTO.setResponseCode(HttpStatus.UNAUTHORIZED.value());
        }
        return responseDTO;
    }
}