package com.arghyam.backend.controller;


import com.arghyam.backend.dto.LoginAndRegisterResponseMap;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.dto.ResponseDTO;
import com.arghyam.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping(value = "/user/getUserProfile")
    public LoginAndRegisterResponseMap getUser(@Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws IOException {
        return userService.getUserProfile(requestDTO, bindingResult);
    }


    @PostMapping(value = "/createRegistryUser")
    public LoginAndRegisterResponseMap completeSignUp(@Validated @RequestBody RequestDTO requestDTO,
                                 BindingResult bindingResult) throws IOException {
        return userService.createRegistryUser(requestDTO, bindingResult);
    }


    @GetMapping(value = "/getRegisteredUsers")
    public LoginAndRegisterResponseMap getRegistereUsers() throws IOException {
        return userService.getRegistereUsers();
    }


    @PostMapping(value = "/createDischargeData")
    public LoginAndRegisterResponseMap createDischargeData(@Validated @RequestBody RequestDTO requestDTO,
                                                      BindingResult bindingResult) throws IOException {
        return userService.createDischargeData(requestDTO, bindingResult);
    }

    @RequestMapping(value = "/user/profilePicture", method = RequestMethod.PUT, headers = "Content-Type= multipart/form-data")
    public ResponseDTO uploadFile(
                           @RequestParam("file") MultipartFile file) {
         return userService.updateProfilePicture(file);
    }


    @PostMapping(value = "/createSpring")
    public LoginAndRegisterResponseMap createSpring(@Validated @RequestBody RequestDTO requestDTO,
                                                           BindingResult bindingResult) throws IOException {
        return userService.createSpring(requestDTO, bindingResult);
    }
}
