package com.example.shared;

public abstract class SQLRepository {

    protected SQLRepository() {
    }

    protected static String toLikeFull(Object value) {
        return "%" + value + "%";
    }
}