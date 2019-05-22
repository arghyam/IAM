package com.arghyam.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserCreateException extends RuntimeException {

    /**
     * Instantiates a new UserCreateException .
     *
     * @param message the message
     */
    public UserCreateException(String message) {
        super(message);
    }

}

