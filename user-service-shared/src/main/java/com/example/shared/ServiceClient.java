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

    private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VALUE = "application/json";

    protected static final TypeReference<Void> VOID_TYPE_REFERENCE =
        new TypeReference<>() {
        };

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

        return sendRequestOrThrow(request, returnType);
    }

    protected <B, R> R sendPost(URI uri, B requestBody, TypeReference<R> returnType) {
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .POST(HttpRequest.BodyPublishers.ofByteArray(serializeRequestBodyOrThrow(requestBody)))
            .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
            .build();

        return sendRequestOrThrow(request, returnType);
    }

    protected <R> R sendDelete(URI uri, TypeReference<R> returnType) {
        var request = HttpRequest.newBuilder()
            .uri(uri)
            .DELETE()
            .header(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE)
            .build();

        return sendRequestOrThrow(request, returnType);
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

    private <T> T sendRequestOrThrow(HttpRequest request, TypeReference<T> returnType) {
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return objectMapper.readValue(response.body(), returnType);
        } catch (IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ServiceClientException(exception);
        }
    }

    private <T> byte[] serializeRequestBodyOrThrow(T object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException exception) {
            throw new ServiceClientException(exception);
        }
    }
}