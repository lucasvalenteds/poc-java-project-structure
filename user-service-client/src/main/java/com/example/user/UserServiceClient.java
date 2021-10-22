package com.example.user;

import com.example.shared.ServiceClient;
import com.example.shared.ServiceException;
import com.example.shared.ServiceResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.UUID;

public final class UserServiceClient extends ServiceClient implements UserService {

    private static final TypeReference<UserResponse> USER_RESPONSE_TYPE_REFERENCE =
        new TypeReference<>() {
        };

    private static final TypeReference<ServiceResponse<UserResponse>> SERVICE_RESPONSE_TYPE_REFERENCE =
        new TypeReference<>() {
        };

    private final URI endpointUri;

    public UserServiceClient(HttpClient httpClient, ObjectMapper objectMapper, URI serverUri) {
        super(httpClient, objectMapper);
        this.endpointUri = serverUri.resolve("/users");
    }

    @Override
    public UserResponse create(UserRequest userRequest) throws ServiceException {
        return sendPost(endpointUri, userRequest, USER_RESPONSE_TYPE_REFERENCE);
    }

    @Override
    public UserResponse remove(UUID id) throws ServiceException {
        return sendDelete(endpointUri.resolve(String.valueOf(id)), USER_RESPONSE_TYPE_REFERENCE);
    }

    @Override
    public UserResponse findById(UUID id) throws ServiceException {
        return sendGet(endpointUri.resolve(String.valueOf(id)), USER_RESPONSE_TYPE_REFERENCE);
    }

    @Override
    public ServiceResponse<UserResponse> findAll(UserResponseFilter filter) throws ServiceException {
        var queryParameters = new HashMap<String, Object>();
        queryParameters.put("name", filter.getName());
        queryParameters.put("age", filter.getName());

        return sendGet(endpointUri.resolve(createQueryUri(queryParameters)), SERVICE_RESPONSE_TYPE_REFERENCE);
    }
}