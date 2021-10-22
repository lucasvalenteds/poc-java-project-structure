package com.example.user;

import com.example.shared.ServiceException;
import com.example.shared.ServiceResponse;

import java.util.UUID;

public interface UserService {

    UserResponse create(UserRequest userRequest) throws ServiceException;

    UserResponse remove(UUID id) throws ServiceException;

    UserResponse findById(UUID id) throws ServiceException;

    ServiceResponse<UserResponse> findAll(UserResponseFilter filter) throws ServiceException;
}