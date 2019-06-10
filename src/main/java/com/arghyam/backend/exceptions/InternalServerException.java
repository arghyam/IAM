package com.arghyam.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalServerException extends RuntimeException{
    private final String message;


    /**
     * Instantiates a new Bad request exception.
     *
     * @param message the message
     */
    public InternalServerException(String message) {
        super(message);
        this.message = message;
    }

    /**
     *
     * @return message
     */

    @Override
    public String getMessage() {
        return message;
    }
}
