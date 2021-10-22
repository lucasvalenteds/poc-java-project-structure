package com.example.shared;

import java.util.UUID;

public final class ServiceResourceException extends ServiceException {

    private final UUID resourceId;

    public ServiceResourceException(UUID resourceId, Throwable throwable) {
        super("Resource not found by ID", throwable);
        this.resourceId = resourceId;
    }

    public UUID getResourceId() {
        return resourceId;
    }
}