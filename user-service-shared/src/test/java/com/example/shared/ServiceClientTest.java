package com.example.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void testHandlingJsonSerializationError(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        var requestBody = ServiceClientTestBuilder.createRequestBodyFromJsonFile("item-malformed.json");
        ServiceClientTestBuilder.mockMalformedPostRequest();

        var exception = assertThrows(
            ServiceClientException.class,
            () -> serviceClient.sendPost(serverUri, requestBody, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals("Client could not communicate with the server", exception.getMessage());
    }

    @Test
    void testHandlingJsonDeserializationError(WireMockRuntimeInfo wireMockRuntimeInfo) throws IOException {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        var requestBody = ServiceClientTestBuilder.createRequestBodyFromJsonFile("item-invalid.json");
        ServiceClientTestBuilder.mockMalformedPostResponse();

        var exception = assertThrows(
            ServiceClientException.class,
            () -> serviceClient.sendPost(serverUri, requestBody, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals("Client could not communicate with the server", exception.getMessage());
    }

    @Test
    void testHandlingServerError(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var serverUri = URI.create(wireMockRuntimeInfo.getHttpBaseUrl());
        ServiceClientTestBuilder.mockServerErrorResponse();

        var exception = assertThrows(
            ServiceClientException.class,
            () -> serviceClient.sendGet(serverUri, ServiceClientTestBuilder.MESSAGE_TYPE_REFERENCE)
        );

        assertEquals("Client could not communicate with the server", exception.getMessage());
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
}