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
        var userTable = new UserTable(UUID.randomUUID(), userTableInsert.firstName(), userTableInsert.age());

        var query = "insert into " + UserTable.TABLE + " (" + UserTable.COLUMNS + ") values (?, ?, ?)";
        var objects = new Object[]{userTable.id(), userTable.firstName(), userTable.age()};
        var types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER};

        var rowsAffected = jdbcTemplate.update(query, objects, types);
        var expectedRowsAffected = 1;
        if (rowsAffected != expectedRowsAffected) {
            throw new IncorrectResultSizeDataAccessException(expectedRowsAffected, rowsAffected);
        }

        return userTable;
    }

    @Transactional
    public void remove(UUID id) {
        var userResponse = findById(id);

        var query = "delete from " + UserTable.TABLE + " where " + UserTable.ID + " = ?";
        var objects = new Object[]{userResponse.id()};
        var types = new int[]{Types.VARCHAR};

        int rowsAffected = jdbcTemplate.update(query, objects, types);
        var expectedRowsAffected = 1;
        if (rowsAffected != expectedRowsAffected) {
            throw new IncorrectResultSizeDataAccessException(expectedRowsAffected, rowsAffected);
        }
    }

    public UserTable findById(UUID id) {
        var query = "select " + UserTable.COLUMNS + " from " + UserTable.TABLE + " where " + UserTable.ID + " = ?";

        var objects = new Object[]{id};
        var types = new int[]{Types.VARCHAR};

        return jdbcTemplate.queryForObject(query, objects, types, UserRepository::mapRow);
    }

    public List<UserTable> findAll(UserResponseFilter filter) {
        var query = "select " + UserTable.COLUMNS + " from " + UserTable.TABLE;

        var filters = new StringBuilder();
        var objects = new ArrayList<>();
        var types = new ArrayList<Integer>();
        writeQueryFilters(filters, objects, types, filter);

        var primitiveObjects = objects.toArray();
        var primitiveTypes = types.stream()
            .mapToInt(it -> it)
            .toArray();

        return jdbcTemplate.query(query + filters, primitiveObjects, primitiveTypes, UserRepository::mapRow);
    }

    public Long count(UserResponseFilter filter) {
        var query = "select count(1) from " + UserTable.TABLE;

        var filters = new StringBuilder();
        var objects = new ArrayList<>();
        var types = new ArrayList<Integer>();
        writeQueryFilters(filters, objects, types, filter);

        var primitiveObjects = objects.toArray();
        var primitiveTypes = types.stream()
            .mapToInt(it -> it)
            .toArray();

        return jdbcTemplate.queryForObject(query + filters, primitiveObjects, primitiveTypes, Long.class);
    }

    public Boolean existsByName(String name) {
        var query = "select exists ( select 1 from " + UserTable.TABLE + " where " + UserTable.FIRST_NAME + " = ?)";

        var objects = new Object[]{name};
        var types = new int[]{Types.VARCHAR};

        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, objects, types, Boolean.class));
    }

    private void writeQueryFilters(StringBuilder query,
                                   List<Object> objects,
                                   List<Integer> types,
                                   UserResponseFilter filter) {
        var filters = new ArrayList<String>();

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