package com.arghyam.backend.dao.Impl;

import com.arghyam.backend.config.AppContext;
import com.arghyam.backend.dao.KeycloakDAO;
import com.arghyam.backend.dto.AccessTokenResponseDTO;
import com.arghyam.backend.dto.LoginDTO;
import com.arghyam.backend.dto.LoginResponseDTO;
import com.arghyam.backend.dto.ResponseDTO;
import com.arghyam.backend.exceptions.ForbiddenException;
import com.arghyam.backend.exceptions.NotFoundException;
import com.arghyam.backend.exceptions.UnauthorizedException;
import com.arghyam.backend.exceptions.UserCreateException;
import com.arghyam.backend.dao.KeycloakService;
import com.arghyam.backend.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@Component
@Service
public class KeycloakServiceImpl implements KeycloakService{

    @Autowired
    private KeycloakDAO keycloakDao;


    @Autowired
    private KeycloakService keycloakService;


    @Autowired
    private AppContext appContext;

    @Autowired
    private KeycloakDAO keycloakDAO;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void register(String token, UserRepresentation userRepresentation) throws IOException {
        Call<Void> loginResponseDTOCall = keycloakDao.registerUser(appContext.getRealm(), "Bearer " + token, userRepresentation);
        Response loginResponseDTOResponse = loginResponseDTOCall.execute();

        if (!loginResponseDTOResponse.isSuccessful()) {
            if (loginResponseDTOResponse.code() == 401) {
                throw new UnauthorizedException("User is not Authorized");
            } else if (loginResponseDTOResponse.code() == 409) {
                throw new UserCreateException("User already exists");
            }else if (loginResponseDTOResponse.code()==403){
                throw new ForbiddenException("Forbidden");
            }
        }
    }


    @Override
    public String generateAccessTokenFromUserName(String username) throws IOException {
            AccessTokenResponseDTO adminAccessTokenResponse = keycloakDao.generateAccessTokenUsingCredentials(appContext.getRealm(), appContext.getAdminUserName(),
                    appContext.getAdminUserpassword(), appContext.getClientId(), appContext.getGrantType(), null).execute().body();
            return adminAccessTokenResponse.getAccessToken();

    }


    @Override
    public UserRepresentation getUserByUsername(String token, String username, String realm) throws IOException {

        Call<List<UserRepresentation>> userRepresentationCall = keycloakDao.searchUsers(realm, "Bearer " + token, username);

        Response<List<UserRepresentation>> response = userRepresentationCall.execute();

        if (!response.isSuccessful()) {
            if (response.code() == 401) {
                throw new UnauthorizedException("User is not Authorized");
            } else if (response.code() == 404) {
                return null;
            }else if (response.code()==403){
                throw new ForbiddenException("forbidden");
            }
        }
        if (response.body().isEmpty() || response.body().size() == 0) {
            return null;
        }
        return response.body().get(0);
    }


    @Override
    public AccessTokenResponseDTO refreshAccessToken(LoginDTO loginDTO) {
        Call<AccessTokenResponseDTO> loginResponseDTOCall ;
        Response<AccessTokenResponseDTO> loginResponseDTO ;
        try {

            if(Constants.REFRESH_TOKEN.equalsIgnoreCase(loginDTO.getGrantType())) {
                loginResponseDTOCall = keycloakDao.refreshAccessToken(appContext.getRealm(), loginDTO.getRefreshToken(),
                        appContext.getClientId(), loginDTO.getGrantType(), appContext.getClientSecret());
                loginResponseDTO = loginResponseDTOCall.execute();
                return loginResponseDTO.body();
            }
            else {
                loginResponseDTOCall = keycloakDao.login(appContext.getRealm(), loginDTO.getUsername(), loginDTO.getPassword(), appContext.getClientId(),
                        Constants.PASSWORD, appContext.getClientSecret());
                loginResponseDTO = loginResponseDTOCall.execute();
                return loginResponseDTO.body();
            }
        }
        catch(Exception exception) {

        }
        return null;

    }


    @Override
    public ResponseDTO logout(String id) throws IOException {
        ResponseDTO response = new ResponseDTO();
        String adminAccessToken = this.generateAccessTokenFromUserName(appContext.getAdminUserName());
        Call<Void> logoutCall = keycloakDao.logout("Bearer " + adminAccessToken, appContext.getRealm(), id);
        Response logoutResponse = logoutCall.execute();
        if (logoutResponse.isSuccessful()) {
            response.setResponseCode(HttpStatus.OK.value());
        } else {
            response.setResponseCode(HttpStatus.NOT_FOUND.value());
            response.setMessage(logoutResponse.message());
        }
        return response;
    }


    @Override
    public String generateAccessToken(String adminUserName, String adminPassword) throws IOException {
        Call<AccessTokenResponseDTO> accessTokenResponseDTOCall = keycloakDao.generateAccessTokenUsingCredentials(appContext.getRealm(),
                adminUserName, adminPassword, appContext.getClientId(), "password",
                appContext.getClientSecret());

        Response<AccessTokenResponseDTO> response=accessTokenResponseDTOCall.execute();
        if (response.code()==401){
            throw new UnauthorizedException("User is not Authorized");
        }else if (response.code()==404){
            throw new NotFoundException("not found");
        }else if (response.isSuccessful()){
            return response.body().getAccessToken();
        }else {

            return "Internal server error"+response.code();
        }
    }


    @Override
    public void updateUser(String token, String id, UserRepresentation user, String realm) throws IOException {

        Call<ResponseBody> responseBodyCall = keycloakDAO.updateUser("Bearer "+token, id, user, realm);
        Response<ResponseBody> response = responseBodyCall.execute();
        log.info("update user query" + response.code());
    }


    @Override
    public LoginResponseDTO login(UserRepresentation loginRequest, BindingResult bindingResult) throws IOException {
        LoginResponseDTO loginResponseDTO;
        Call<LoginResponseDTO> loginResponseDTOCall = keycloakDao.loginWithScope(appContext.getRealm(), loginRequest.getUsername(),
                Constants.PASSWORD, appContext.getClientId(), "password", appContext.getClientSecret(),"openid profile");
        Response<LoginResponseDTO> response = loginResponseDTOCall.execute();
        if (response.isSuccessful()) {
            loginResponseDTO = response.body();
        } else if (response.code() == 404) {
            throw new NotFoundException("Not found");
        } else if (response.code() == 401) {
            throw new UnauthorizedException("UnAuthorized");
        } else {
            return null;
        }
        return loginResponseDTO;

    }



    @Override
    public List<UserRepresentation> getRegisteredUsers(String token,String realm) throws IOException {
        Call<List<UserRepresentation>> userRepresentationCall = keycloakDao.getRegisteredUsers(realm, "Bearer " + token);
        Response<List<UserRepresentation>> response = userRepresentationCall.execute();
        if (!response.isSuccessful()) {
            if (response.code() == 401) {
                throw new UnauthorizedException("User is not Authorized");
            } else if (response.code() == 404) {
                return null;
            }else if (response.code()==403){
                throw new ForbiddenException("forbidden");
            }
        }
        if (response.body().isEmpty() || response.body().size() == 0) {
            return null;
        }
        return response.body();
    }

}
