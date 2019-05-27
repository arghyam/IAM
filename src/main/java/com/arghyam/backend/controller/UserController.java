package com.arghyam.backend.controller;


import com.arghyam.backend.dto.LoginAndRegisterResponseMap;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public LoginAndRegisterResponseMap reSendOTP(@Validated @RequestBody RequestDTO requestDTO, BindingResult bindingResult) throws IOException {

        return userService.reSendOtp(requestDTO, bindingResult);
    }

    @PostMapping(value = "/user/updateUserProfile")
    public LoginAndRegisterResponseMap updateUser(@Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws IOException {
          return userService.updateUserProfile(requestDTO, bindingResult);
    }

    @GetMapping(value = "/user/getUserProfile")
    public LoginAndRegisterResponseMap getUser(@Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws IOException {
        return userService.getUserProfile(requestDTO, bindingResult);
    }

}
