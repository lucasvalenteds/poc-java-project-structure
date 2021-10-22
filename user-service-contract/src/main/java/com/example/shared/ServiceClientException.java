package com.example.shared;

import java.io.Serial;

public final class ServiceClientException extends ServiceException {

    @Serial
    private static final long serialVersionUID = -713657365719832130L;

    public ServiceClientException(Throwable throwable) {
        super("Client could not communicate with the server", throwable);
    }
}