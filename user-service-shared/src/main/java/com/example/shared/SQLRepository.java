package com.example.shared;

import java.util.List;

public abstract class SQLRepository {

    protected SQLRepository() {
    }

    protected String toLikeFull(Object value) {
        return "%" + value + "%";
    }

    protected void writeWhereClause(StringBuilder query, List<String> filters) {
        if (!filters.isEmpty()) {
            query.append(" where ")
                .append(String.join(" and ", filters));
        }
    }
}