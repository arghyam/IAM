package com.arghyam.backend.service.ServiceImpl;

import com.arghyam.backend.config.AppContext;
import com.arghyam.backend.dao.KeycloakDAO;
import com.arghyam.backend.dao.RegistryDAO;
import com.arghyam.backend.dto.*;
import com.arghyam.backend.entity.RegistryUser;
import com.arghyam.backend.entity.Springuser;
import com.arghyam.backend.exceptions.*;
import com.arghyam.backend.repositories.UserRepository;
import com.arghyam.backend.dao.KeycloakService;
import com.arghyam.backend.service.UserService;
import com.arghyam.backend.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.keycloak.admin.client.Keycloak;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

import static java.util.Arrays.asList;

@Component
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    AppContext appContext;

    @Autowired
    UserRepository userRepository;

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

    ObjectMapper mapper = new ObjectMapper();


    @Override
    public LoginResponseDTO createUsers(RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        validatePojo(bindingResult);
        UserRepresentation registerResponseDTO = mapper.convertValue(requestDTO.getRequest().get("person"), UserRepresentation.class);
        try {
            String userToken = keycloakService.generateAccessToken(appContext.getAdminUserName(), appContext.getAdminUserpassword());
            UserRepresentation userRepresentation= keycloakService.getUserByUsername(userToken, registerResponseDTO.getUsername(), appContext.getRealm());
            if (null!=userRepresentation){
                throw new  UserAlreadyExistsException("User already exists");
            }
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue("password");
            credential.setTemporary(false);
            registerResponseDTO.setEnabled(Boolean.TRUE);      // A disabled user cannot login.
            registerResponseDTO.setCredentials(asList(credential));
            keycloakService.register(userToken,registerResponseDTO);

        } catch (Exception e) {
            System.out.println("exception"+e);
        }
        return null;
    }

    @Override
    public List<Springuser> fetchSpringUsers() {
      return userRepository.findAll();
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
}
