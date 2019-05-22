package com.arghyam.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * The type UnprocessableEntity Exception.
 * <p>Exception Class for throwing UnprocessableEntitiesException with custom error message
 * Annotated with {@link ResponseStatus @ResponseStatus which marks
 * this exception class with the status 422
 * </p>**
 * <p>
 *@JsonIgnoreProperties is used to either suppress serialization of properties (during
 * serialization), or ignore processing of JSON properties read (during deserialization). </p>
 * <p>
 * @ResponseStatus the status code is applied to the HTTP response when the handler
 * method is invoked and overrides status information
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableEntitiesException extends RuntimeException {

    private final List<ValidationError> errors;

    /**
     * Instantiates a new Unprocessable entity exception.
     *
     * @param errors the errors
     */
    public UnprocessableEntitiesException(List<ValidationError> errors) {
        this.errors = errors;
    }

    /**
     * Gets errors.
     *
     * @return the errors
     */
    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Sets errors.
     *
     * @param errors the errors
     */
}
