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

    @PutMapping(value = "/users/profile/{profileId}")
    public LoginAndRegisterResponseMap updateUser(@ApiParam(value = "profileId", example = "01234567-89ab-cdef-0123-456789abcdef", required = true)
                                                      @PathVariable(value = "profileId") String profileId, @ApiParam(value = "userProfile", required = true, name="userProfile") @Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws IOException {
          return userService.updateUserProfile(profileId, requestDTO, bindingResult);
    }

    @PostMapping(value = "/user/getUserProfile")
    public LoginAndRegisterResponseMap getUser(@ApiParam(value = "userProfile", required = true, name="userProfile") @Validated @RequestBody RequestDTO requestDTO,
                                                  BindingResult bindingResult) throws Exception {
        return userService.getUserProfile(requestDTO, bindingResult);
    }


    @PostMapping(value = "/createRegistryUser")
    public LoginAndRegisterResponseMap completeSignUp(@ApiParam(value = "registeryUser", required = true, name="registeryUser")   @Validated @RequestBody RequestDTO requestDTO,
                                 BindingResult bindingResult) throws IOException {
        return userService.createRegistryUser(requestDTO, bindingResult);
    }


    @GetMapping(value = "/users")
    public LoginAndRegisterResponseMap getRegistereUsers() throws IOException {
        return userService.getRegistereUsers();
    }


    @PostMapping(value = "/springs/{springCode}/discharge")
    public LoginAndRegisterResponseMap createDischargeData(@ApiParam(value = "springCode", example = "012345", required = true)
                                                               @PathVariable(value = "springCode") String springCode, @ApiParam(value = "dischargeData", required = true, name="dischargeData")  @Validated @RequestBody RequestDTO requestDTO,
                                                      BindingResult bindingResult) throws IOException {
        return userService.createDischargeData(springCode, requestDTO, bindingResult);
    }

    @RequestMapping(value = "/user/profilePicture", method = RequestMethod.PUT, headers = "Content-Type= multipart/form-data")
    public ResponseDTO uploadFile(
                           @ApiParam(value = "file", example = "file.xlsx", required = true)
                           @RequestParam("file") MultipartFile file) {
         return userService.updateProfilePicture(file);
    }


    @PostMapping(value = "/spring")
    public LoginAndRegisterResponseMap createSpring(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO,
                                                           BindingResult bindingResult) throws IOException {
        return userService.createSpring(requestDTO, bindingResult);
    }



    @PostMapping(value = "/springs/{springCode}/additionalInfo")
    public LoginAndRegisterResponseMap createAdditionalInfo(@ApiParam(value = "springCode", example = "012345", required = true)
                                                                @PathVariable(value = "springCode") String springCode,
                                                            @ApiParam(value = "addtitionalData", required = true, name="addtitionalData") @Validated @RequestBody RequestDTO requestDTO,
                                                    BindingResult bindingResult) throws IOException {
        return userService.createAdditionalInfo(springCode, requestDTO, bindingResult);
    }

    @PostMapping(value = "/springById")
    public Object getSpringById(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO) throws IOException {
        return userService.getSpringById(requestDTO);
    }

    @PostMapping(value = "/getSprings")
    public LoginAndRegisterResponseMap getAllSprings(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO,
                                                     @ApiParam(value = "pageNumber", required = true, name="pageNumber")
                                                     @RequestParam(value = "pageNumber",required = false, defaultValue = "") Integer pageNumber,
                                                     BindingResult bindingResult) throws IOException {
        return userService.getAllSprings(requestDTO, bindingResult, pageNumber);
    }


    @PostMapping(value = "/getAdditionalDetailsForSpring")
    public LoginAndRegisterResponseMap getAdditionalDetailsForSpring(@ApiParam(value = "spring", required = true, name="spring") @Validated @RequestBody RequestDTO requestDTO,
                                                     BindingResult bindingResult) throws IOException {
        return userService.getAdditionalDetailsForSpring(requestDTO, bindingResult);
    }


    @PostMapping(value = "/reviewerData")
    public LoginAndRegisterResponseMap reviewerData( @Validated @RequestBody RequestDTO requestDTO, BindingResult bindingResult) throws IOException {
        return userService.reviewerData(requestDTO, bindingResult);
    }


    @RequestMapping(value = "/notifications/{userId}", method = RequestMethod.POST)
    LoginAndRegisterResponseMap myActivities(@ApiParam(value = "generate accessToken body", required = true,
            name="generate accessToken body")@RequestBody  RequestDTO requestDTO,
                                             @PathVariable(value = "userId") String userId) throws IOException {

        return userService.getAllNotifications(requestDTO,userId);
    }


    @RequestMapping(value = "/notificationCount/{userId}", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getNotificationCount(@ApiParam(value = "generate accessToken body", required = true,
            name="generate accessToken body")@RequestBody  RequestDTO requestDTO,
                                             @PathVariable(value = "userId") String userId) throws IOException {

        return userService.getNotificationCount(requestDTO,userId);
    }


}
