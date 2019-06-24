package org.forwater.backend.dao;

import org.forwater.backend.dto.RegistryRequest;
import org.forwater.backend.dto.RegistryResponse;
import org.forwater.backend.utils.Constants;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.IOException;

public interface RegistryDAO {

    @POST(Constants.REGISRY_ADD_USER)
    Call<RegistryResponse> createUser(@Header("x-authenticated-user-token") String adminAccessToken,
                                      @Body RegistryRequest registryRequest) throws IOException;

    @POST(Constants.REGISRY_SEARCH_USER)
    Call<RegistryResponse> findSpringbyId(@Header("x-authenticated-user-token") String adminAccessToken
            ,@Body RegistryRequest registryRequest ) throws IOException;

    @POST(Constants.REGISRY_SEARCH_USER)
    Call<RegistryResponse> searchUser(@Header("x-authenticated-user-token") String adminAccessToken,
                                      @Body RegistryRequest registryRequest) throws IOException;

}
