package com.example.shared;

public final class ServiceClientException extends ServiceException {

    public ServiceClientException(Throwable throwable) {
        super("Client could not communicate with the server", throwable);
    }
}