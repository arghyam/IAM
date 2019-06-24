package org.forwater.backend.controller;


import io.swagger.annotations.ApiParam;
import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;
import org.forwater.backend.dto.ResponseDTO;
import org.forwater.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping(path = "/api/v1")
@RestController
public class UserController {

    @Autowired
    UserService userService;


    @PostMapping(value = "/user/register")
    public void createUser(@ApiParam(value = "user", required = true, name="user") @Validated @RequestBody RequestDTO requestDTO, String userToken,
                                       BindingResult bindingResult) throws IOException {
          userService.createUsers(requestDTO, userToken, bindingResult);
    }


    @PostMapping(value = "/sendOTP")
    public LoginAndRegisterResponseMap reSendOTP(@ApiParam(value = "sentOtp", required = true, name="sentOtp") @Validated @RequestBody RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        return userService.reSendOtp(requestDTO, bindingResult);
    }

    @PostMapping(value = "/user/updateUserProfile")
    public LoginAndRegisterResponseMap updateUser(@ApiParam(value = "userProfile", required = true, name="userProfile") @Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws IOException {
          return userService.updateUserProfile(requestDTO, bindingResult);
    }

    @PostMapping(value = "/user/getUserProfile")
    public LoginAndRegisterResponseMap getUser(@ApiParam(value = "userProfile", required = true, name="userProfile") @Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws IOException {
        return userService.getUserProfile(requestDTO, bindingResult);
    }


    @PostMapping(value = "/createRegistryUser")
    public LoginAndRegisterResponseMap completeSignUp(@ApiParam(value = "registeryUser", required = true, name="registeryUser")   @Validated @RequestBody RequestDTO requestDTO,
                                 BindingResult bindingResult) throws IOException {
        return userService.createRegistryUser(requestDTO, bindingResult);
    }


    @GetMapping(value = "/getRegisteredUsers")
    public LoginAndRegisterResponseMap getRegistereUsers() throws IOException {
        return userService.getRegistereUsers();
    }


    @PostMapping(value = "/createDischargeData")
    public LoginAndRegisterResponseMap createDischargeData(@ApiParam(value = "dischargeData", required = true, name="dischargeData")  @Validated @RequestBody RequestDTO requestDTO,
                                                      BindingResult bindingResult) throws IOException {
        return userService.createDischargeData(requestDTO, bindingResult);
    }

    @RequestMapping(value = "/user/profilePicture", method = RequestMethod.PUT, headers = "Content-Type= multipart/form-data")
    public ResponseDTO uploadFile(
                           @ApiParam(value = "file", example = "file.xlsx", required = true)
                           @RequestParam("file") MultipartFile file) {
         return userService.updateProfilePicture(file);
    }


    @PostMapping(value = "/createSpring")
    public LoginAndRegisterResponseMap createSpring(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO,
                                                           BindingResult bindingResult) throws IOException {
        return userService.createSpring(requestDTO, bindingResult);
    }



    @PostMapping(value = "/createAdditionalInfo")
    public LoginAndRegisterResponseMap createAdditionalInfo(@ApiParam(value = "addtitionalData", required = true, name="addtitionalData") @Validated @RequestBody RequestDTO requestDTO,
                                                    BindingResult bindingResult) throws IOException {
        return userService.createAdditionalInfo(requestDTO, bindingResult);
    }

    @PostMapping(value = "/springs")
    public Object getSpringById(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO) throws IOException {
        return userService.getSpringById(requestDTO);
    }

    @PostMapping(value = "/getAllSprings")
    public LoginAndRegisterResponseMap getAllSprings(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO,
                                                     @ApiParam(value = "pageNumber", required = true, name="pageNumber")
                                                     @RequestParam(value = "pageNumber", defaultValue = "") Integer pageNumber,
                                                     BindingResult bindingResult) throws IOException {
        return userService.getAllSprings(requestDTO, bindingResult, pageNumber);
    }

}
