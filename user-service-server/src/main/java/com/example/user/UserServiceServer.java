package com.example.user;

import com.example.shared.ServiceException;
import com.example.shared.ServiceResourceException;
import com.example.shared.ServiceResponse;
import com.example.shared.ServiceValidationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.UUID;

public final class UserServiceServer implements UserService {

    private final UserRepository userRepository;

    public UserServiceServer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse create(UserRequest userRequest) throws ServiceException {
        if (userRepository.existsByName(userRequest.name())) {
            throw new ServiceValidationException(UserServiceErrors.UserAlreadyExist);
        }

        return userRepository.insert(userRequest.name(), userRequest.age());
    }

    @Override
    public UserResponse remove(UUID id) throws ServiceException {
        try {
            return userRepository.remove(id);
        } catch (IncorrectResultSizeDataAccessException exception) {
            throw new ServiceResourceException(id, exception);
        }
    }

    @Override
    public UserResponse findById(UUID id) throws ServiceException {
        try {
            return userRepository.findById(id);
        } catch (IncorrectResultSizeDataAccessException exception) {
            throw new ServiceResourceException(id, exception);
        }
    }

    @Override
    public ServiceResponse<UserResponse> findAll(UserResponseFilter filter) throws ServiceException {
        var users = userRepository.findAll(filter);
        var total = userRepository.count(filter);

        return new ServiceResponse<>(users, total);
    }
}