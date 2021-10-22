package com.example.shared;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
class ServiceExceptionController {

    @GetMapping("/service")
    public void throwServiceException() {
        throw new ServiceException("Some error happened", new RuntimeException("Parent error"));
    }

    @GetMapping("/resource/{id}")
    public void throwServiceResourceException(@PathVariable UUID id) {
        throw new ServiceResourceException(id, new IncorrectResultSizeDataAccessException(1, 0));
    }

    @GetMapping("/validation")
    public void throwServiceValidationException() {
        throw new ServiceValidationException(new ServiceError(123, "Testing validation errors"));
    }
}