package org.forwater.backend.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.forwater.backend.config.AppContext;
import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;
import org.forwater.backend.dto.ResponseDTO;
import org.forwater.backend.exceptions.UserCreateException;
import org.forwater.backend.service.LoginService;
import org.forwater.backend.utils.KeycloakUtil;
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

import java.io.IOException;


@RestController
@RequestMapping(value = "api/v1", produces = {"application/json", "application/x-www-form-urlencoded"}, consumes = {"application/json", "application/x-www-form-urlencoded"})
public class LoginController {

    @Autowired
    LoginService loginService;

    @Autowired
    AppContext appContext;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @ApiOperation(value="api to login", notes="api that authenticates a user and allows to login")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 404, message = "Not found"),
            @ApiResponse(code = 422, message = "Unprocessable values"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public LoginAndRegisterResponseMap login( @ApiParam(value = "login body", required = true, name="login body")
                                              @Validated @RequestBody RequestDTO requestDTO,
                        BindingResult bindingResult) throws IOException {
        return loginService.login(requestDTO, bindingResult);
    }



    @RequestMapping(value = "/user/generate-accesstoken", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public LoginAndRegisterResponseMap generateAccessToken(@ApiParam(value = "generate accessToken body", required = true, name="generate accessToken body")
                                                           @RequestBody RequestDTO requestDTO) throws Exception {
            return loginService.refreshAccessToken(requestDTO);
    }



    @RequestMapping(value = "/user/verifyOtp", method = RequestMethod.POST)
    LoginAndRegisterResponseMap verifyOtp(@ApiParam(value = "verify otp body", required = true, name="verify otp body")
                                          @Validated @RequestBody RequestDTO requestDTO, BindingResult bindingResult)throws IOException {

        return loginService.verifyOtp(requestDTO,bindingResult);
    }



    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/logout", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE, produces = MediaType.ALL_VALUE)
    public ResponseDTO logout(@ApiParam(name = "Authorization", value = "A valid access token", required = true)
                              @RequestHeader("access-token") String accessToken) throws IOException, VerificationException {
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