package com.arghyam.backend.service;

import com.arghyam.backend.dto.AccessTokenResponseDTO;
import com.arghyam.backend.dto.LoginResponseDTO;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.dto.UserRegisterDTO;
import com.arghyam.backend.entity.Springuser;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;

public interface UserService {

    public List<Springuser> fetchSpringUsers();

    public Keycloak getKeycloak();

    public void validatePojo(BindingResult bindingResult);

    public void createUsers(RequestDTO requestDTO, String userToken, BindingResult bindingResult) throws IOException;

    public String otpgenerator();

}
