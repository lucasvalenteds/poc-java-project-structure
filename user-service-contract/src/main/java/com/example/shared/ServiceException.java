package com.example.shared;

import java.io.Serial;

public sealed class ServiceException
    extends RuntimeException
    permits ServiceClientException, ServiceResourceException, ServiceValidationException {

    @Serial
    private static final long serialVersionUID = 4806492994395898541L;

    public ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}