package com.example.user;

import com.example.shared.ServiceError;

public final class UserServiceErrors {

    public static final ServiceError UserAlreadyExist =
        new ServiceError(1001, "Another user with same name already exists");

    private UserServiceErrors() {
    }
}