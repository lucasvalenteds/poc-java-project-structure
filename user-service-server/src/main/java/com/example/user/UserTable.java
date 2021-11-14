package com.example.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table(UserTable.TABLE)
public record UserTable(
    @Id @Column(ID) UUID id,
    @Column(FIRST_NAME) String firstName,
    @Column(AGE) Integer age
) {
    public static final String TABLE = "USERS";
    public static final String ID = "ID";
    public static final String FIRST_NAME = "FIRST_NAME";
    public static final String AGE = "AGE";
    public static final String COLUMNS = String.join(", ", List.of(ID, FIRST_NAME, AGE));
}
