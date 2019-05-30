package com.arghyam.backend.dao;

import com.arghyam.backend.dto.RegistryRequest;
import com.arghyam.backend.dto.RegistryResponse;
import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.utils.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.io.IOException;

public interface RegistryDAO {

    @POST(Constants.REGISRY_ADD_USER)
    Call<RegistryResponse> createUser(@Header("x-authenticated-user-token") String adminAccessToken,
                                      @Body RegistryRequest registryRequest) throws IOException;


//    @POST(Constants.REGISRY_SEARCH_USER)
//    Call<RegistryResponse> searchUser(@Header("x-authenticated-user-token") String accessToken,
//                                      @Body SlimRegistryUserDto slimRegistryUserDto ) throws IOException;
//
//    @POST(Constants.REGISTRY_UPDATE_USER)
//    Call<RegistryResponse> updateUser(@Header("x-authenticated-user-token") String accessToken,
//                                      @Body RegistryRequestWithOsId registryRequestWithOsId) throws IOException;

}
