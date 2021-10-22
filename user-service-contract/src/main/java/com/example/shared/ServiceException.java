package com.example.shared;

public sealed class ServiceException
    extends RuntimeException
    permits ServiceClientException, ServiceResourceException, ServiceValidationException {

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}