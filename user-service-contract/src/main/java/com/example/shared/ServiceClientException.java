package com.example.shared;

import java.io.Serial;
import java.util.Optional;

public final class ServiceClientException extends ServiceException {

    @Serial
    private static final long serialVersionUID = 3629353911598008520L;

    private final Integer statusCode;
    private final String url;

    public ServiceClientException(Throwable throwable) {
        super("Client could not communicate with the server", throwable);
        this.statusCode = null;
        this.url = null;
    }

    public ServiceClientException(Integer statusCode, String url, Throwable throwable) {
        super("Client could not handle the server response", throwable);
        this.statusCode = statusCode;
        this.url = url;
    }

    public Optional<Integer> getStatusCode() {
        return Optional.ofNullable(statusCode);
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }
}