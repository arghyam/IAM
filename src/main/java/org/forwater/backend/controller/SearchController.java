package org.forwater.backend.controller;

import io.swagger.annotations.ApiParam;
import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;
import org.forwater.backend.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequestMapping(path = "/api/v1")
@RestController
public class SearchController {

    @Autowired
    SearchService searchService;

    @RequestMapping(value = "/state", method = RequestMethod.POST)
    LoginAndRegisterResponseMap postStates(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.postStates(requestDTO);
    }


    @RequestMapping(value = "/getStates", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getStates(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getStates(requestDTO,"1");
    }


    @RequestMapping(value = "/getStateByName", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getStateByName(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getStateByName(requestDTO);
    }
}
