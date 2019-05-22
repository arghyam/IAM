package com.arghyam.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type User access forbidden.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    private final String message;

    /**
     * Instantiates a new Internal server error exception.
     *
     * @param message the message
     */
    public ForbiddenException(String message) {
        super(message);
        this.message = message;
    }


    public String getMessage() {
        return this.message;
    }
}

