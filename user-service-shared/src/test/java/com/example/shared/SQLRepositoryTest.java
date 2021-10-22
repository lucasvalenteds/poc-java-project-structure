package com.example.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLRepositoryTest {

    @Test
    void testToLikeFull() {
        var value = "Smith";

        var text = SQLRepository.toLikeFull(value);

        assertEquals("%Smith%", text);
    }
}