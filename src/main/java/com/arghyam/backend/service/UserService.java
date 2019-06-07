package com.arghyam.backend.service;

import com.arghyam.backend.dto.LoginAndRegisterResponseMap;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.dto.ResponseDTO;
import org.keycloak.admin.client.Keycloak;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    public Keycloak getKeycloak();

    public void validatePojo(BindingResult bindingResult);

    public void createUsers(RequestDTO requestDTO, String userToken, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap updateUserProfile(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public String otpgenerator();

    public LoginAndRegisterResponseMap getUserProfile(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap reSendOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap createRegistryUser(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap getRegistereUsers() throws IOException;

    public LoginAndRegisterResponseMap createDischargeData(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap createSpring(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public ResponseDTO updateProfilePicture(MultipartFile file);

    LoginAndRegisterResponseMap createAdditionalInfo(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;
}
