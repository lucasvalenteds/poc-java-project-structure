package com.example.shared;

import java.io.Serial;

public final class ServiceValidationException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 266882223514544254L;

    private final ServiceError error;

    public ServiceValidationException(ServiceError error, Throwable throwable) {
        super(error.toString(), throwable);
        this.error = error;
    }

    public ServiceValidationException(ServiceError error) {
        this(error, null);
    }

    public ServiceError getError() {
        return error;
    }
}