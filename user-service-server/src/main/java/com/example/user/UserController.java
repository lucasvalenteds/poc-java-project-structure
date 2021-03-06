package com.example.user;

import com.example.shared.ServiceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public final class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userService.create(userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> remove(@PathVariable("id") UUID id) {
        userService.remove(id);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.findById(id));
    }

    @GetMapping
    public ResponseEntity<ServiceResponse<UserResponse>> findAll(UserResponseFilter filter) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.findAll(filter));
    }
}