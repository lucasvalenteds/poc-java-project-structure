package com.example.shared;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SQLRepositoryTest {

    private final SQLRepository repository = new SQLRepositoryStub();

    @Test
    void testToLikeFull() {
        var value = "Smith";

        var text = repository.toLikeFull(value);

        assertEquals("%Smith%", text);
    }

    @Test
    void testWritingWhereClauseWithoutFilters() {
        var query = new StringBuilder();
        var clauses = List.<String>of();

        repository.writeWhereClause(query, clauses);

        assertEquals("", query.toString());
    }

    @Test
    void testWritingWhereClauseWithSingleFilter() {
        var query = new StringBuilder();
        var clauses = List.of("NAME = ?");

        repository.writeWhereClause(query, clauses);

        assertEquals(" where NAME = ?", query.toString());
    }

    @Test
    void testWritingWhereClauseWithMultipleFilters() {
        var query = new StringBuilder();
        var clauses = List.of("NAME like ?", "AGE = ?", "ACTIVE = true");

        repository.writeWhereClause(query, clauses);

        assertEquals(" where NAME like ? and AGE = ? and ACTIVE = true", query.toString());
    }
}