package com.arghyam.backend.exceptions;

import com.arghyam.backend.dto.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The type Global exception handler.
 * @ControllerAdvice
 * @RestController Annotation is used to perform http web requests
 * @Slf4j Annotation is used to cause lombok to generate a logger field
 * @ControllerAdvice is declared explicitly as Spring beans or auto-detected via classpath scanning.
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    /**
     * Handle bad request exception response entity.
     *
     * @param e        the e
     * @param response the response
     * @return the response entity
     * @throws IOException the io exception
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequestException(BadRequestException e,
                                                    HttpServletResponse response)
            throws IOException {
        return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handleUserAccessForbidden(ForbiddenException e,
                                                    HttpServletResponse response)
            throws IOException {
        return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleUserAccessForbidden(UnauthorizedException e,
                                                    HttpServletResponse response)
            throws IOException {
        return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity internalServerException(UnauthorizedException e,
                                                    HttpServletResponse response)
            throws IOException {
        return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

