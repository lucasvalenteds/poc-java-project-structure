package com.example.shared;

public final class ServiceValidationException extends ServiceException {

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