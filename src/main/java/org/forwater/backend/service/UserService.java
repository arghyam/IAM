package org.forwater.backend.service;

import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;
import org.forwater.backend.dto.ResponseDTO;
import org.keycloak.admin.client.Keycloak;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    public Keycloak getKeycloak();

    public void validatePojo(BindingResult bindingResult);

    public void createUsers(RequestDTO requestDTO, String userToken, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap updateUserProfile(String userId, RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public String otpgenerator();

    public LoginAndRegisterResponseMap getUserProfile(RequestDTO requestDTO, BindingResult bindingResult) throws Exception;

    public LoginAndRegisterResponseMap reSendOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap createRegistryUser(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap getRegistereUsers() throws IOException;

    public LoginAndRegisterResponseMap createDischargeData(String springCode, RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap createSpring(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public ResponseDTO updateProfilePicture(MultipartFile file);

    LoginAndRegisterResponseMap createAdditionalInfo(String sprinceCode, RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    Object getSpringById( RequestDTO springId) throws IOException;

    public LoginAndRegisterResponseMap getAllSprings(RequestDTO requestDTO, BindingResult bindingResult, Integer pageNumber) throws IOException;

    public LoginAndRegisterResponseMap getAdditionalDetailsForSpring(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap reviewerData(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;


    LoginAndRegisterResponseMap getAllNotifications(RequestDTO requestDTO, String userId) throws IOException;

    LoginAndRegisterResponseMap getNotificationCount(RequestDTO requestDTO, String userId) throws IOException;
}
