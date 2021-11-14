package com.example.user;

import com.example.shared.ServiceException;
import com.example.shared.ServiceResourceException;
import com.example.shared.ServiceResponse;
import com.example.shared.ServiceValidationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.util.UUID;
import java.util.stream.Collectors;

public final class UserServiceServer implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceServer(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse create(UserRequest userRequest) throws ServiceException {
        if (userRepository.existsByName(userRequest.name())) {
            throw new ServiceValidationException(UserServiceErrors.UserAlreadyExist);
        }

        var userTableInsert = userMapper.map(userRequest);
        var userTable = userRepository.insert(userTableInsert);

        return userMapper.map(userTable);
    }

    @Override
    public void remove(UUID id) throws ServiceException {
        try {
            userRepository.remove(id);
        } catch (IncorrectResultSizeDataAccessException exception) {
            throw new ServiceResourceException(id, exception);
        }
    }

    @Override
    public UserResponse findById(UUID id) throws ServiceException {
        try {
            var userTable = userRepository.findById(id);

            return userMapper.map(userTable);
        } catch (IncorrectResultSizeDataAccessException exception) {
            throw new ServiceResourceException(id, exception);
        }
    }

    @Override
    public ServiceResponse<UserResponse> findAll(UserResponseFilter filter) throws ServiceException {
        var users = userRepository.findAll(filter).stream()
            .map(userMapper::map)
            .collect(Collectors.toList());

        var total = userRepository.count(filter);

        return new ServiceResponse<>(users, total);
    }
}