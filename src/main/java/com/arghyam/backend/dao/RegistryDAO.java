package com.arghyam.backend.dao;

import com.arghyam.backend.dto.RequestDTO;
import com.arghyam.backend.utils.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import java.io.IOException;

public interface RegistryDAO {

    @POST(Constants.REGISRY_ADD_USER)
    Call<Void> createUser(@Header("x-authenticated-user-token") String token,
                          @Body RequestDTO registryUserDto ) throws IOException;
}
