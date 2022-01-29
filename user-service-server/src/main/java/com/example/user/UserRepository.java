package com.example.user;

import com.example.shared.SQLRepository;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepository extends SQLRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public UserTable insert(UserTableInsert userTableInsert) {
        final var userTable = new UserTable(UUID.randomUUID(), userTableInsert.firstName(), userTableInsert.age());

        final var query = "insert into " + UserTable.TABLE + " (" + UserTable.COLUMNS + ") values (?, ?, ?)";
        final var objects = new Object[]{userTable.id(), userTable.firstName(), userTable.age()};
        final var types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER};

        final var rowsAffected = jdbcTemplate.update(query, objects, types);
        final var expectedRowsAffected = 1;
        if (rowsAffected != expectedRowsAffected) {
            throw new IncorrectResultSizeDataAccessException(expectedRowsAffected, rowsAffected);
        }

        return userTable;
    }

    @Transactional
    public void remove(UUID id) {
        final var userResponse = findById(id);

        final var query = "delete from " + UserTable.TABLE + " where " + UserTable.ID + " = ?";
        final var objects = new Object[]{userResponse.id()};
        final var types = new int[]{Types.VARCHAR};

        int rowsAffected = jdbcTemplate.update(query, objects, types);
        final var expectedRowsAffected = 1;
        if (rowsAffected != expectedRowsAffected) {
            throw new IncorrectResultSizeDataAccessException(expectedRowsAffected, rowsAffected);
        }
    }

    public UserTable findById(UUID id) {
        final var query = "select " + UserTable.COLUMNS + " from " + UserTable.TABLE + " where " + UserTable.ID + " = ?";

        final var objects = new Object[]{id};
        final var types = new int[]{Types.VARCHAR};

        return jdbcTemplate.queryForObject(query, objects, types, UserRepository::mapRow);
    }

    public List<UserTable> findAll(UserResponseFilter filter) {
        final var query = "select " + UserTable.COLUMNS + " from " + UserTable.TABLE;

        final var filters = new StringBuilder();
        final var objects = new ArrayList<>();
        final var types = new ArrayList<Integer>();
        this.writeQueryFilters(filters, objects, types, filter);

        final var sortingAndPaging = new StringBuilder();
        super.writePagingClause(sortingAndPaging, filter);
        super.writeSortingClause(sortingAndPaging, filter);

        final var sql = query + filters + sortingAndPaging;
        final var primitiveObjects = objects.toArray();
        final var primitiveTypes = types.stream()
            .mapToInt(it -> it)
            .toArray();

        return jdbcTemplate.query(sql, primitiveObjects, primitiveTypes, UserRepository::mapRow);
    }

    public Long count(UserResponseFilter filter) {
        final var query = "select count(1) from " + UserTable.TABLE;

        final var filters = new StringBuilder();
        final var objects = new ArrayList<>();
        final var types = new ArrayList<Integer>();
        this.writeQueryFilters(filters, objects, types, filter);

        final var paging = new StringBuilder();
        super.writePagingClause(paging, filter);

        final var sql = query + filters + paging;
        final var primitiveObjects = objects.toArray();
        final var primitiveTypes = types.stream()
            .mapToInt(it -> it)
            .toArray();

        return jdbcTemplate.queryForObject(sql, primitiveObjects, primitiveTypes, Long.class);
    }

    public Boolean existsByName(String name) {
        final var query = "select exists ( select 1 from " + UserTable.TABLE + " where " + UserTable.FIRST_NAME + " = ?)";

        final var objects = new Object[]{name};
        final var types = new int[]{Types.VARCHAR};

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, objects, types, Boolean.class));
    }

    private void writeQueryFilters(StringBuilder query,
                                   List<Object> objects,
                                   List<Integer> types,
                                   UserResponseFilter filter) {
        final var filters = new ArrayList<String>();

        if (filter.getName() != null) {
            filters.add(UserTable.FIRST_NAME + " like ?");
            objects.add(toLikeFull(filter.getName()));
            types.add(Types.VARCHAR);
        }

        if (filter.getAge() != null) {
            filters.add(UserTable.AGE + " = ?");
            objects.add(filter.getAge());
            types.add(Types.INTEGER);
        }

        super.writeWhereClause(query, filters);
    }

    private static UserTable mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new UserTable(
            UUID.fromString(resultSet.getString(UserTable.ID)),
            resultSet.getString(UserTable.FIRST_NAME),
            resultSet.getInt(UserTable.AGE)
        );
    }
}