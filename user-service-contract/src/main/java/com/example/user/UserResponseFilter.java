package com.example.user;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
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
}