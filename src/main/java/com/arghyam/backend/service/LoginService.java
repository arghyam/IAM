package com.arghyam.backend.service;

import com.arghyam.backend.dto.*;
import org.springframework.validation.BindingResult;

import java.io.IOException;

public interface LoginService {

    public UserLoginResponseDTO login(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public AccessTokenResponseDTO refreshAccessToken(RequestDTO requestDTO);

    public void genarateOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public ResponseDTO logout(String id) throws IOException;

    public UserLoginResponseDTO verifyOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;
}
