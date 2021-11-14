package com.example.user;

import com.example.shared.ServiceResponse;

import java.util.List;
import java.util.UUID;

public final class UserTestBuilder {

    private static final UUID ID = UUID.randomUUID();
    private static final String NAME = "John Smith";
    private static final Integer AGE = 45;

    public static final UserTable USER_TABLE = new UserTable(ID, NAME, AGE);
    public static final UserTableInsert USER_TABLE_INSERT = new UserTableInsert(NAME, AGE);

    public static final UUID USER_ID = UserTestBuilder.ID;
    public static final UserRequest USER_REQUEST = new UserRequest(NAME, AGE);
    public static final UserResponse USER_RESPONSE = new UserResponse(ID, NAME, AGE);
    public static final UserResponseFilter USER_RESPONSE_FILTER = new UserResponseFilter();
    public static final ServiceResponse<UserResponse> SERVICE_RESPONSE_USERS =
        new ServiceResponse<>(List.of(USER_RESPONSE), 1L);

    public static final String USER_WITH_SAME_NAME = "Brown";
    private static final UserRequest USER_WITH_SAME_NAME_1 = new UserRequest("Oliver " + USER_WITH_SAME_NAME, 49);
    private static final UserRequest USER_WITH_SAME_NAME_2 = new UserRequest("Dave " + USER_WITH_SAME_NAME, 76);

    public static final Integer USER_WITH_SAME_AGE = 21;
    private static final UserRequest USER_WITH_SAME_AGE_1 = new UserRequest("James Miller", USER_WITH_SAME_AGE);
    private static final UserRequest USER_WITH_SAME_AGE_2 = new UserRequest("Rick Smith", USER_WITH_SAME_AGE);

    public static final List<UserRequest> USER_REQUESTS = List.of(
        USER_WITH_SAME_NAME_1,
        USER_WITH_SAME_NAME_2,
        USER_WITH_SAME_AGE_1,
        USER_WITH_SAME_AGE_2
    );

    private UserTestBuilder() {
    }
}