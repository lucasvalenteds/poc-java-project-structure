package com.example.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ServiceExceptionHandlers {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandlers.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceErrorResponse> handleException(Exception exception) {
        LOGGER.error("Internal Server error", exception);

        var statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(statusCode)
            .body(new ServiceErrorResponse(statusCode.value(), statusCode.getReasonPhrase()));
    }

    @ExceptionHandler(ServiceResourceException.class)
    public ResponseEntity<Void> handleServiceResourceException(ServiceResourceException exception) {
        LOGGER.info(exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .build();
    }

    @ExceptionHandler(ServiceValidationException.class)
    public ResponseEntity<ServiceErrorResponse> handleServiceValidationException(ServiceValidationException exception) {
        LOGGER.info(exception.getMessage());

        var serviceError = exception.getError();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ServiceErrorResponse(serviceError.code(), serviceError.message()));
    }
}