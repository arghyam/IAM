package com.arghyam.backend.service;

import com.arghyam.backend.dto.*;
import org.springframework.validation.BindingResult;

import java.io.IOException;

public interface LoginService {

    public LoginAndRegisterResponseMap login(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public LoginAndRegisterResponseMap refreshAccessToken(RequestDTO requestDTO);

    public void genarateOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    public ResponseDTO logout(String id) throws IOException;

    public LoginAndRegisterResponseMap verifyOtp(RequestDTO requestDTO, BindingResult bindingResult) throws IOException;

    LoginAndRegisterResponseMap myactivities(RequestDTO requestDTO) throws IOException;
}
