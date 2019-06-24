package org.forwater.backend.service;

import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;
import org.forwater.backend.dto.ResponseDTO;
import org.springframework.validation.BindingResult;

import java.io.IOException;

public interface LoginService {

    public LoginAndRegisterResponseMap login(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap refreshAccessToken(RequestDTO requestDTO);

    public void genarateOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public ResponseDTO logout(String id) throws IOException;

    public LoginAndRegisterResponseMap verifyOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;
}
