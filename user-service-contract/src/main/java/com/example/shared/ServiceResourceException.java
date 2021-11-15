package com.example.shared;

import java.io.Serial;
import java.util.Optional;
import java.util.UUID;

public final class ServiceResourceException extends ServiceException {

    @Serial
    private static final long serialVersionUID = -5440496468163986920L;

    private final UUID resourceId;

    public ServiceResourceException() {
        super("Resource not found by ID", null);
        this.resourceId = null;
    }

    public ServiceResourceException(UUID resourceId, Throwable throwable) {
        super("Resource not found by ID", throwable);
        this.resourceId = resourceId;
    }

    public Optional<UUID> getResourceId() {
        return Optional.ofNullable(resourceId);
    }
}