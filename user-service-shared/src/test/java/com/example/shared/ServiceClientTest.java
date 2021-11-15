package com.example.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WireMockTest
class ServiceClientTest {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ServiceClient serviceClient = new ServiceClientStub(httpClient, objectMapper);

    @Test
    void testSendingGet(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockGetResponse();

        var responseBody = serviceClient.sendGet(serverUri, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE);

        assertEquals(ServiceClientTestBuilder.MESSAGE, responseBody);
    }

    @Test
    void testSendingPost(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        var requestBody = ServiceClientTestBuilder.createRequestBodyFromJsonFile("item-valid.json");
        ServiceClientTestBuilder.mockPostResponse();

        var responseBody = serviceClient.sendPost(serverUri, requestBody, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE);

        assertEquals(ServiceClientTestBuilder.MESSAGE, responseBody);
    }

    @Test
    void testSendingDelete(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockDeleteResponse();

        assertDoesNotThrow(() -> serviceClient.sendDelete(serverUri));
    }

    @Test
    void testHandlingBadRequest(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockServiceErrorResponse(400);

        var exception = assertThrows(
            ServiceValidationException.class,
            () -> serviceClient.sendGet(serverUri, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals(1001, exception.getError().code());
        assertEquals("Some error happened", exception.getError().message());
    }

    @Test
    void testHandlingNotFound(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockServiceErrorResponse(404);

        var exception = assertThrows(
            ServiceResourceException.class,
            () -> serviceClient.sendGet(serverUri, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals(Optional.empty(), exception.getResourceId());
        assertEquals("Resource not found by ID", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testHandlingInternalError(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockServiceErrorResponse(500);

        var exception = assertThrows(
            ServiceException.class,
            () -> serviceClient.sendGet(serverUri, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals("Some error happened", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testHandlingUnknownErrors(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockServiceErrorResponse(418);

        var exception = assertThrows(
            ServiceClientException.class,
            () -> serviceClient.sendGet(serverUri, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals(Optional.of(418), exception.getStatusCode());
        assertEquals(Optional.of(wireMockRuntimeInfo.getHttpBaseUrl()), exception.getUrl());
        assertEquals("Client could not handle the server response", exception.getMessage());
    }

    @Test
    void testCreatingQueryUri() {
        var queryParameters = new HashMap<String, Object>();
        queryParameters.put("name", null);
        queryParameters.put("age", 21);
        queryParameters.put("isAdmin", false);

        var uri = serviceClient.createQueryUri(queryParameters);

        assertEquals("/?isAdmin=false&age=21", uri);
    }

    @Test
    void testCreatingQueryUriWithPaginationAndSorting() {
        var queryParameters = new HashMap<String, Object>();
        var filter = new ServiceResponseFilter<>()
            .page(2L)
            .size(10L)
            .sort("name")
            .sortDirection("asc");

        var uri = serviceClient.createQueryUri(queryParameters, filter);

        assertEquals("/?sortDirection=asc&size=10&page=2&sort=name", uri);
    }
}