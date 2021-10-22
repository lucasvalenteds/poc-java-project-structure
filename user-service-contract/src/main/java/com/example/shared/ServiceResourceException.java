package com.example.shared;

import java.io.Serial;
import java.util.UUID;

public final class ServiceResourceException extends ServiceException {

    @Serial
    private static final long serialVersionUID = -5569048300427614625L;

    private final UUID resourceId;

    public ServiceResourceException(UUID resourceId, Throwable throwable) {
        super("Resource not found by ID", throwable);
        this.resourceId = resourceId;
    }

    public UUID getResourceId() {
        return resourceId;
    }
}