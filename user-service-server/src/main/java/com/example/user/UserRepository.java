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

    private static final String TABLE = "USERS";
    private static final String ID = "ID";
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String AGE = "AGE";
    private static final String COLUMNS = String.join(", ", List.of(ID, FIRST_NAME, AGE));

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public UserResponse insert(UUID id, String name, Integer age) {
        var query = "insert into " + TABLE + " (" + COLUMNS + ") values (?, ?, ?)";
        var objects = new Object[]{id, name, age};
        var types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER};

        var rowsAffected = jdbcTemplate.update(query, objects, types);
        var expectedRowsAffected = 1;
        if (rowsAffected != expectedRowsAffected) {
            throw new IncorrectResultSizeDataAccessException(expectedRowsAffected, rowsAffected);
        }

        return new UserResponse(id, name, age);
    }

    @Transactional
    public void remove(UUID id) {
        var userResponse = findById(id);

        var query = "delete from " + TABLE + " where " + ID + " = ?";
        var objects = new Object[]{userResponse.id()};
        var types = new int[]{Types.VARCHAR};

        int rowsAffected = jdbcTemplate.update(query, objects, types);
        var expectedRowsAffected = 1;
        if (rowsAffected != expectedRowsAffected) {
            throw new IncorrectResultSizeDataAccessException(expectedRowsAffected, rowsAffected);
        }
    }

    public UserResponse findById(UUID id) {
        var query = "select " + COLUMNS + " from " + TABLE + " where " + ID + " = ?";

        var objects = new Object[]{id};
        var types = new int[]{Types.VARCHAR};

        return jdbcTemplate.queryForObject(query, objects, types, UserRepository::mapRow);
    }

    public List<UserResponse> findAll(UserResponseFilter filter) {
        var query = "select " + COLUMNS + " from " + TABLE;

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
        var query = "select count(1) from " + TABLE;

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
        var query = "select exists ( select 1 from " + TABLE + " where " + FIRST_NAME + " = ?)";

        var objects = new Object[]{name};
        var types = new int[]{Types.VARCHAR};

        return jdbcTemplate.queryForObject(query, objects, types, Boolean.class);
    }

    private void writeQueryFilters(StringBuilder query,
                                   List<Object> objects,
                                   List<Integer> types,
                                   UserResponseFilter filter) {
        var filters = new ArrayList<String>();

        if (filter.getName() != null) {
            filters.add(FIRST_NAME + " like ?");
            objects.add(toLikeFull(filter.getName()));
            types.add(Types.VARCHAR);
        }

        if (filter.getAge() != null) {
            filters.add(AGE + " = ?");
            objects.add(filter.getAge());
            types.add(Types.INTEGER);
        }

        super.writeWhereClause(query, filters);
    }

    private static UserResponse mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new UserResponse(
            UUID.fromString(resultSet.getString(ID)),
            resultSet.getString(FIRST_NAME),
            resultSet.getInt(AGE)
        );
    }
}