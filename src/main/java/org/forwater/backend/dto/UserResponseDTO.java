package org.forwater.backend.dto;

public class UserResponseDTO {

    private AccessTokenResponseDTO accessTokenResponseDTO;
    private String username;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
