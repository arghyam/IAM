package com.arghyam.backend.controller;


import com.arghyam.backend.dto.AccessTokenResponseDTO;
import com.arghyam.backend.dto.LoginResponseDTO;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.dto.UserRegisterDTO;
import com.arghyam.backend.repositories.UserRepository;
import com.arghyam.backend.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(path = "/api/v1")
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    @PostMapping(value = "/user/register")
    public void createUser(@Validated @RequestBody RequestDTO requestDTO, String userToken,
                                       BindingResult bindingResult) throws IOException {
          userService.createUsers(requestDTO, userToken, bindingResult);
    }

}
