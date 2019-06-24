package org.forwater.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * The type Bad request exception.
 *  </p>
 * @ResponseStatus the status code is applied to the HTTP response when the handler
 * method is invoked and overrides status information
 * </p>
 * <p>
 * @JsonIgnoreProperties is used to either suppress serialization of
 * properties (during serialization), or ignore processing of
 * JSON properties read (during deserialization).
 * </p>
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BadRequestException extends RuntimeException {

    /**
     * Instantiates a new Unauthorized exception.
     *
     * @param message the message
     */
    public BadRequestException(String message) {
        super(message);
    }
}
