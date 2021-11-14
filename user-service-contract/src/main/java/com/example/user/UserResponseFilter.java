package com.example.user;

import com.example.shared.ServiceResponseFilter;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class UserResponseFilter extends ServiceResponseFilter<UserResponseFilter> {

    @Setter
    private String name;

    @Setter
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