package com.example.shared;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

final class ServiceClientStub extends ServiceClient {

    ServiceClientStub(HttpClient httpClient, ObjectMapper objectMapper) {
        super(httpClient, objectMapper);
    }
}