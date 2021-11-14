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

    @Test
    void testWritingPagingClauseWithLimit() {
        var query = new StringBuilder();
        var filter = new ServiceResponseFilter<>()
            .size(10L);

        repository.writePagingClause(query, filter);

        assertEquals(" limit 10", query.toString());
    }

    @Test
    void testWritingPagingClauseWithLimitAndOffset() {
        var query = new StringBuilder();
        var filter = new ServiceResponseFilter<>()
            .size(10L)
            .page(2L);

        repository.writePagingClause(query, filter);

        assertEquals(" limit 10 offset 2", query.toString());
    }

    @Test
    void testWritingPagingClauseIgnoresOffsetWhenLimitNotInformed() {
        var query = new StringBuilder();
        var filter = new ServiceResponseFilter<>()
            .page(2L);

        repository.writePagingClause(query, filter);

        assertEquals("", query.toString());
    }

    @Test
    void testWritingSortingClauseWithSort() {
        var query = new StringBuilder();
        var filter = new ServiceResponseFilter<>()
            .sort("name");

        repository.writeSortingClause(query, filter);

        assertEquals(" order by name", query.toString());
    }

    @Test
    void testWritingSortingClauseWithSortAndDirection() {
        var query = new StringBuilder();
        var filter = new ServiceResponseFilter<>()
            .sort("age")
            .sortDirection("desc");

        repository.writeSortingClause(query, filter);

        assertEquals(" order by age desc", query.toString());
    }

    @Test
    void testWritingSortingClauseIgnoresDirectionWhenSortNotInformed() {
        var query = new StringBuilder();
        var filter = new ServiceResponseFilter<>()
            .sortDirection("asc");

        repository.writeSortingClause(query, filter);

        assertEquals("", query.toString());
    }
}