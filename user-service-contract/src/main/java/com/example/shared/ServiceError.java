package com.example.shared;

import java.io.Serializable;

public final record ServiceError(Integer code, String message) implements Serializable {
}