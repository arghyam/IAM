package com.arghyam.backend.dto;

public class UserLoginResponseDTO {

    private UserResponseDTO response;
    private String message;
    private int responseCode;


    public UserResponseDTO getResponse() {
        return response;
    }

    public void setResponse(UserResponseDTO response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
