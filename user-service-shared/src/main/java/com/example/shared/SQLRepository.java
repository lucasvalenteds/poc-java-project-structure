package com.example.shared;

import java.util.List;

public abstract class SQLRepository {

    protected SQLRepository() {
    }

    protected String toLikeFull(Object value) {
        return "%" + value + "%";
    }

    protected void writePagingClause(StringBuilder query, ServiceResponseFilter<?> filter) {
        var limit = filter.getSize();
        var offset = filter.getPage();

        if (limit != null) {
            query.append(" limit ").append(limit);

            if (offset != null) {
                query.append(" offset ").append(offset);
            }
        }
    }

    protected void writeSortingClause(StringBuilder query, ServiceResponseFilter<?> filter) {
        var sort = filter.getSort();
        var direction = filter.getSortDirection();

        if (sort != null) {
            query.append(" order by ").append(sort);

            if (direction != null) {
                query.append(' ').append(direction);
            }
        }
    }

    protected void writeWhereClause(StringBuilder query, List<String> filters) {
        if (!filters.isEmpty()) {
            query.append(" where ")
                .append(String.join(" and ", filters));
        }
    }
}