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
    Call<RegistryResponse> findEntitybyId(@Header("x-authenticated-user-token") String adminAccessToken
            ,@Body RegistryRequest registryRequest ) throws IOException;

    @POST(Constants.REGISRY_SEARCH_USER)
    Call<RegistryResponse> searchUser(@Header("x-authenticated-user-token") String adminAccessToken,
                                      @Body RegistryRequest registryRequest) throws IOException;

    @POST(Constants.REGISTRY_UPDATE_USER)
    Call<RegistryResponse> updateUser(@Header("x-authenticated-user-token") String adminAccessToken,
                                      @Body RegistryRequest registryRequest) throws IOException;
//
//    @DELETE(Constants.REGISRY_DELETE_ENTRY)
//    Call<RegistryResponse> deleteEntity(@Header("x-authenticated-user-token") String adminAccessToken,@Path("osid") String id) throws IOException;
//
    @POST(Constants.REGISRY_DELETE_ENTRY)
    Call<RegistryResponse> deleteUser(@Header("x-authenticated-user-token") String adminAccessToken,
                                      @Body RegistryRequest registryRequest) throws IOException;

}
