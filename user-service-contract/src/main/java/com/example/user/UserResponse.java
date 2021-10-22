package com.example.user;

import java.util.UUID;

public final record UserResponse(UUID id, String name, Integer age) {
}