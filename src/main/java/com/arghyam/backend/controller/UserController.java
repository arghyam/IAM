package com.arghyam.backend.controller;


import com.arghyam.backend.dto.LoginAndRegisterResponseMap;
import com.arghyam.backend.dto.LoginResponseDTO;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.dto.ResponseDTO;
import com.arghyam.backend.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping(path = "/api/v1")
@RestController
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping(value = "/user/register")
    public void createUser(@Validated @RequestBody RequestDTO requestDTO, String userToken,
                                       BindingResult bindingResult) throws IOException {
          userService.createUsers(requestDTO, userToken, bindingResult);
    }


    @PostMapping(value = "/sendOTP")
    public LoginAndRegisterResponseMap reSendOTP(@Validated @RequestBody RequestDTO requestDTO, BindingResult bindingResult) throws IOException{

        return userService.reSendOtp(requestDTO,bindingResult);
    }

}
