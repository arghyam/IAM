package com.arghyam.backend.service;

import com.arghyam.backend.dto.LoginAndRegisterResponseMap;
import com.arghyam.backend.dto.RequestDTO;
import org.keycloak.admin.client.Keycloak;
import org.springframework.validation.BindingResult;

import java.io.IOException;

public interface UserService {

    public Keycloak getKeycloak();

    public void validatePojo(BindingResult bindingResult);

    public void createUsers(RequestDTO requestDTO, String userToken, BindingResult bindingResult) throws IOException;

    public String otpgenerator();

    LoginAndRegisterResponseMap reSendOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;
}
