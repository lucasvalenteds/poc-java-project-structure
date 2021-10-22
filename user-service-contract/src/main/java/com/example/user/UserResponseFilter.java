package com.example.user;

import java.util.Objects;

public final class UserResponseFilter {

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public UserResponseFilter name(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public UserResponseFilter age(Integer age) {
        this.age = age;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponseFilter that = (UserResponseFilter) o;
        return Objects.equals(name, that.name) && Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}