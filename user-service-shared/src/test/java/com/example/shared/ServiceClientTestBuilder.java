package com.example.shared;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ServiceClientTestBuilder {

    public static final TypeReference<ServiceClientMessage> MESSAGE_TYPE_REFERENCE = new TypeReference<>() {
    };

    public static final ServiceClientMessage MESSAGE = new ServiceClientMessage("Hello World!");

    private ServiceClientTestBuilder() {
    }

    public static void mockGetResponse() {
        var responseDefinition = ResponseDefinitionBuilder.responseDefinition()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("item-valid.json");

        var mapping = WireMock.get(UrlPattern.ANY)
            .willReturn(responseDefinition);

        WireMock.stubFor(mapping);
    }

    public static void mockPostResponse() {
        var responseDefinition = ResponseDefinitionBuilder.responseDefinition()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("item-valid.json");

        var mapping = WireMock.post(UrlPattern.ANY)
            .willReturn(responseDefinition);

        WireMock.stubFor(mapping);
    }

    public static void mockDeleteResponse() {
        var responseDefinition = ResponseDefinitionBuilder.responseDefinition()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("item-valid.json");

        var mapping = WireMock.delete(UrlPattern.ANY)
            .willReturn(responseDefinition);

        WireMock.stubFor(mapping);
    }

    public static void mockMalformedPostRequest() {
        var responseDefinition = ResponseDefinitionBuilder.responseDefinition()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBodyFile("item-malformed.json");

        var mapping = WireMock.post(UrlPattern.ANY)
            .willReturn(responseDefinition);

        WireMock.stubFor(mapping);
    }

    public static void mockMalformedPostResponse() {
        var responseDefinition = ResponseDefinitionBuilder.responseDefinition()
            .withStatus(200)
            .withHeader("Content-Type", "application/json");

        var mapping = WireMock.post(UrlPattern.ANY)
            .willReturn(responseDefinition);

        WireMock.stubFor(mapping);
    }

    public static void mockServerErrorResponse() {
        var responseDefinition = ResponseDefinitionBuilder.responseDefinition()
            .withStatus(500)
            .withHeader("Content-Type", "application/json");

        var mapping = WireMock.get(UrlPattern.ANY)
            .willReturn(responseDefinition);

        WireMock.stubFor(mapping);
    }

    public static String createRequestBodyFromJsonFile(String filename) throws IOException {
        var path = Path.of("src", "test", "resources")
            .resolve("__files")
            .resolve(filename);

        return Files.readString(path);
    }
}