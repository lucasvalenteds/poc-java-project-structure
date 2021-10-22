package com.example.shared;

import java.util.List;

public final record ServiceResponse<T>(List<T> items, Long total) {
}