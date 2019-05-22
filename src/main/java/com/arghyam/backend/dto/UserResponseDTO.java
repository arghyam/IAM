package com.arghyam.backend.dto;

public class UserResponseDTO {

    private AccessTokenResponseDTO accessTokenResponseDTO;
    private String username;

    public AccessTokenResponseDTO getAccessTokenResponseDTO() {
        return accessTokenResponseDTO;
    }

    public void setAccessTokenResponseDTO(AccessTokenResponseDTO accessTokenResponseDTO) {
        this.accessTokenResponseDTO = accessTokenResponseDTO;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
