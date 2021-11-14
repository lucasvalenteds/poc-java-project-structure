package com.example.user;

public final class UserMapper {

    public UserTableInsert map(UserRequest userRequest) {
        return new UserTableInsert(
            userRequest.name(),
            userRequest.age()
        );
    }

    public UserResponse map(UserTable userTable) {
        return new UserResponse(
            userTable.id(),
            userTable.firstName(),
            userTable.age()
        );
    }
}
