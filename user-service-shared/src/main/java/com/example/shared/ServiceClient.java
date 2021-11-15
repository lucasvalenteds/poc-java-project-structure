package com.example.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ServiceClient {

    private static final TypeReference<ServiceErrorResponse> SERVICE_ERROR_RESPONSE_TYPE_REFERENCE =
        new TypeReference<>() {
        };

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    protected ServiceClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    protected <R> R sendGet(URI uri, TypeReference<R> returnType) {
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
            .build();

        var response = sendRequestOrThrow(request);
        validateResponse(response);

        return deserializeResponseBodyOrThrow(response, returnType);
    }

    protected <B, R> R sendPost(URI uri, B requestBody, TypeReference<R> returnType) {
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .POST(HttpRequest.BodyPublishers.ofByteArray(serializeRequestBodyOrThrow(requestBody)))
            .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
            .build();

        var response = sendRequestOrThrow(request);
        validateResponse(response);

        return deserializeResponseBodyOrThrow(response, returnType);
    }

    protected void sendDelete(URI uri) {
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .DELETE()
            .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
            .build();

        var response = sendRequestOrThrow(request);
        validateResponse(response);
    }

    protected String createQueryUri(Map<String, Object> queryParameters) {
        return queryParameters.entrySet().stream()
            .filter(queryParameter -> queryParameter.getValue() != null)
            .map(queryParameter -> queryParameter.getKey() + "=" + queryParameter.getValue())
            .collect(Collectors.joining("&", "/?", ""));
    }

    protected String createQueryUri(Map<String, Object> queryParameters, ServiceResponseFilter<?> filter) {
        var parameters = new HashMap<>(queryParameters);
        parameters.put("page", filter.getPage());
        parameters.put("size", filter.getSize());
        parameters.put("sort", filter.getSort());
        parameters.put("sortDirection", filter.getSortDirection());

        return this.createQueryUri(parameters);
    }

    private HttpResponse<byte[]> sendRequestOrThrow(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ServiceClientException(exception);
        }
    }

    private void validateResponse(HttpResponse<byte[]> response) {
        switch (response.statusCode()) {
            case 200:
            case 201:
            case 202:
                return;
            case 400:
                var validation = deserializeResponseBodyOrThrow(response, SERVICE_ERROR_RESPONSE_TYPE_REFERENCE);
                throw new ServiceValidationException(new ServiceError(validation.code(), validation.message()));
            case 404:
                throw new ServiceResourceException();
            case 500:
                var internal = deserializeResponseBodyOrThrow(response, SERVICE_ERROR_RESPONSE_TYPE_REFERENCE);
                throw new ServiceException(internal.message(), null);
            default:
                throw new ServiceClientException(response.statusCode(), response.uri().toString(), null);
        }
    }

    private <T> byte[] serializeRequestBodyOrThrow(T object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException exception) {
            throw new ServiceClientException(exception);
        }
    }

    private <T> T deserializeResponseBodyOrThrow(HttpResponse<byte[]> response, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(response.body(), typeReference);
        } catch (IOException exception) {
            throw new ServiceClientException(response.statusCode(), response.uri().toString(), exception);
        }
    }
}