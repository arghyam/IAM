package com.arghyam.backend.dao;

import com.arghyam.backend.dto.AccessTokenResponseDTO;
import com.arghyam.backend.dto.LoginResponseDTO;
import com.arghyam.backend.utils.Constants;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import javax.ws.rs.Consumes;
import java.io.IOException;
import java.util.List;


public interface KeycloakDAO {

    @GET(Constants.SEARCH_USERS)
    Call<List<UserRepresentation>> searchUsersByUserName(@Path("realm") String realm,
                                                         @Header("Authorization") String token,
                                                         @Query("username") String username) throws IOException;


    @GET(Constants.SEARCH_USERS)
    Call<List<UserRepresentation>> searchUsers(@Path("realm") String realm,
                                               @Header("Authorization") String token,
                                               @Query("username") String username ) throws IOException;


    @POST(Constants.GENERATE_ACCESS_TOKEN)
    @FormUrlEncoded
    Call<AccessTokenResponseDTO> generateAccessTokenUsingCredentials(@Path("realm") String realm, @Field("username") String username,
                                                                     @Field("password") String password,
                                                                     @Field("client_id") String clientId,
                                                                     @Field("grant_type") String grantType,
                                                                     @Field("client_secret") String clientSecret);


    @POST(Constants.LOGIN_API)
    @FormUrlEncoded
    Call<AccessTokenResponseDTO> login(@Path("realm") String realm, @Field("username") String username,
                                       @Field("password") String password,
                                       @Field("client_id") String clientId,
                                       @Field("grant_type") String grantType,
                                       @Field("client_secret") String clientSecret);


    @POST(Constants.KEYCLOAK_REGISTER_API)
    Call<Void> registerUser(@Path("realm") String realm,
                            @Header("Authorization") String token,
                            @Body UserRepresentation userss ) throws IOException;



    @POST(Constants.LOGIN_API)
    @FormUrlEncoded
    Call<AccessTokenResponseDTO> refreshAccessToken(@Path("realm") String realm,
                                                    @Field("refresh_token") String refreshToken,
                                                    @Field("client_id") String clientId,
                                                    @Field("grant_type") String grantType,
                                                    @Field("client_secret") String clientSecret);


    @POST(Constants.LOGOUT_API)
    Call<Void> logout(@Header("Authorization") String token, @Path("realm") String realm, @Path("id") String id);


    @POST(Constants.GENERATE_ACCESS_TOKEN)
    @FormUrlEncoded
    Call<AccessTokenResponseDTO> generateAccessTokenUsingRefreshToken(@Path("realm") String realm, @Field("refresh_token") String refreshToken,
                                                                      @Field("client_id") String clientId,
                                                                      @Field("grant_type") String grantType,
                                                                      @Field("client_secret") String clientSecret);


    @PUT(Constants.UPDATE_USER)
    Call<ResponseBody> updateUser(@Header("Authorization") String token,
                                  @Path("id") String id,
                                  @Body UserRepresentation user,
                                  @Path("realm") String realm) throws IOException;



    @POST(Constants.LOGIN_API)
    @FormUrlEncoded
    Call<LoginResponseDTO> loginWithScope(@Path("realm") String realm, @Field("username") String username,
                                 @Field("password") String password,
                                 @Field("client_id") String clientId,
                                 @Field("grant_type") String grantType,
                                 @Field("client_secret") String clientSecret,
                                 @Field("scope") String scope);

}