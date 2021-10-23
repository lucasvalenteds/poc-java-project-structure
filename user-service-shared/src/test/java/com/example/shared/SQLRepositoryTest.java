package com.example.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLRepositoryTest {

    private final SQLRepository repository = new SQLRepositoryStub();

    @Test
    void testToLikeFull() {
        var value = "Smith";

        var text = repository.toLikeFull(value);

        assertEquals("%Smith%", text);
    }
}