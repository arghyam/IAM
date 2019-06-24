package com.arghyam.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class LoginAndRegisterResponseBody {

    Boolean newUserCreated;
    String message;

    public Boolean getNewUserCreated() {
        return newUserCreated;
    }

    public void setNewUserCreated(Boolean newUserCreated) {
        this.newUserCreated = newUserCreated;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
