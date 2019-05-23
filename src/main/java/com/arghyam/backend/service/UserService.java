package com.arghyam.backend.service;

import com.arghyam.backend.dto.LoginResponseDTO;
import com.arghyam.backend.dto.RequestDTO;
import org.keycloak.admin.client.Keycloak;
import org.springframework.validation.BindingResult;

import java.io.IOException;

public interface UserService {

    public Keycloak getKeycloak();

    public void validatePojo(BindingResult bindingResult);

    public LoginResponseDTO createUsers(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public String otpgenerator();

}
