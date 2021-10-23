package com.example.shared;

public abstract class SQLRepository {

    protected SQLRepository() {
    }

    protected String toLikeFull(Object value) {
        return "%" + value + "%";
    }
}