package org.forwater.backend.controller;

import io.swagger.annotations.ApiParam;
import org.forwater.backend.dto.LoginAndRegisterResponseMap;
import org.forwater.backend.dto.RequestDTO;
import org.forwater.backend.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/getDistricts", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getDistricts(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO)
            throws IOException {

        return searchService.getDistricts(requestDTO,"1");
    }

    @RequestMapping(value = "/getSubDistricts", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getSubDistricts(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO)
            throws IOException {

        return searchService.getSubDistricts(requestDTO,"1");
    }

    @RequestMapping(value = "/getDistrictsByStateOSID", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getDistrictsByStateOSID(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getDistrictsByStateOSID(requestDTO);
    }

    @RequestMapping(value = "/getSubDistrictsByDistrictOSID", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getSubDistrictsByDistrictOSID(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getSubDistrictsByDistrictOSID(requestDTO);
    }
    @RequestMapping(value = "/getVillagesBySubDistrictOSID", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getVillagesBySubDistrictOSID(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getVillagesBySubDistrictOSID(requestDTO);
    }

    @RequestMapping(value = "/getCitiesBySubDistrictOSID", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getCitiesBySubDistrictOSID(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getCitiesBySubDistrictOSID(requestDTO);
    }


    @RequestMapping(value = "/getStateByName", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getStateByName(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getStateByName(requestDTO);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    LoginAndRegisterResponseMap search(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.search(requestDTO);
    }

    @RequestMapping(value = "/getRecentSearches", method = RequestMethod.POST)
    LoginAndRegisterResponseMap getRecentSearches(@ApiParam(value = "generate accessToken body", required = true,
            name = "generate accessToken body") @RequestBody RequestDTO requestDTO) throws IOException {

        return searchService.getRecentSearches(requestDTO);
    }

}
