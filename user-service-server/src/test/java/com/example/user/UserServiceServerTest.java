package com.example.user;

import com.example.shared.ServiceResourceException;
import com.example.shared.ServiceValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceServerTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserServiceServer(userRepository);

    @Test
    void testCreating() {
        var userRequest = UserTestBuilder.USER_REQUEST;
        var userResponse = UserTestBuilder.USER_RESPONSE;

        Mockito.when(userRepository.existsByName(userRequest.name()))
            .thenReturn(false);
        Mockito.when(userRepository.insert(
            Mockito.any(UUID.class),
            Mockito.eq(userRequest.name()),
            Mockito.eq(userRequest.age()))
        ).thenReturn(userResponse);

        var userCreated = userService.create(userRequest);

        assertEquals(userResponse, userCreated);
        Mockito.verify(userRepository, Mockito.times(1))
            .existsByName(userRequest.name());
        Mockito.verify(userRepository, Mockito.times(1))
            .insert(Mockito.any(UUID.class), Mockito.eq(userRequest.name()), Mockito.eq(userRequest.age()));
    }

    @Test
    void testCreatingWithSameNameThrows() {
        var userRequest = UserTestBuilder.USER_REQUEST;

        Mockito.when(userRepository.existsByName(userRequest.name()))
            .thenReturn(true);

        var exception = assertThrows(
            ServiceValidationException.class,
            () -> userService.create(userRequest)
        );

        assertEquals(UserServiceErrors.UserAlreadyExist, exception.getError());
        Mockito.verify(userRepository, Mockito.times(1))
            .existsByName(userRequest.name());
        Mockito.verify(userRepository, Mockito.times(0))
            .insert(Mockito.any(UUID.class), Mockito.eq(userRequest.name()), Mockito.eq(userRequest.age()));
    }

    @Test
    void testRemoving() {
        var userResponse = UserTestBuilder.USER_RESPONSE;

        Mockito.when(userRepository.remove(userResponse.id()))
            .thenReturn(userResponse);

        var userRemoved = userService.remove(userResponse.id());

        assertEquals(userResponse, userRemoved);
        Mockito.verify(userRepository, Mockito.times(1))
            .remove(userResponse.id());
    }

    @Test
    void testRemovingWithUnknownIdThrows() {
        var userResponse = UserTestBuilder.USER_RESPONSE;
        var userId = userResponse.id();

        Mockito.when(userRepository.remove(userId))
            .thenThrow(new IncorrectResultSizeDataAccessException(1, 0));

        var exception = assertThrows(
            ServiceResourceException.class,
            () -> userService.remove(userId)
        );

        assertEquals("Resource not found by ID", exception.getMessage());
        assertEquals(userId, exception.getResourceId());
        Mockito.verify(userRepository, Mockito.times(1))
            .remove(userId);
    }

    @Test
    void testFindingById() {
        var userResponse = UserTestBuilder.USER_RESPONSE;
        var userId = userResponse.id();

        Mockito.when(userRepository.findById(userId))
            .thenReturn(userResponse);

        var userFound = userService.findById(userId);

        assertEquals(userResponse, userFound);
        Mockito.verify(userRepository, Mockito.times(1))
            .findById(userId);
    }

    @Test
    void testFindingByUnknownIdThrows() {
        var userResponse = UserTestBuilder.USER_RESPONSE;
        var userId = userResponse.id();

        Mockito.when(userRepository.findById(userId))
            .thenThrow(new IncorrectResultSizeDataAccessException(1));

        var exception = assertThrows(
            ServiceResourceException.class,
            () -> userService.findById(userId)
        );

        assertEquals("Resource not found by ID", exception.getMessage());
        assertEquals(userId, exception.getResourceId());
        Mockito.verify(userRepository, Mockito.times(1))
            .findById(userId);
    }

    @Test
    void testFindingAll() {
        var filter = UserTestBuilder.USER_RESPONSE_FILTER;
        var userResponses = UserTestBuilder.USER_REQUESTS.stream()
            .map(userRequest -> new UserResponse(UUID.randomUUID(), userRequest.name(), userRequest.age()))
            .toList();
        var amountOfUserResponses = Long.valueOf(userResponses.size());

        Mockito.when(userRepository.findAll(filter))
            .thenReturn(userResponses);
        Mockito.when(userRepository.count(filter))
            .thenReturn(amountOfUserResponses);

        var serviceResponse = userService.findAll(filter);

        assertEquals(userResponses, serviceResponse.items());
        assertEquals(amountOfUserResponses, serviceResponse.total());
        Mockito.verify(userRepository, Mockito.times(1))
            .findAll(filter);
        Mockito.verify(userRepository, Mockito.times(1))
            .count(filter);
    }
}