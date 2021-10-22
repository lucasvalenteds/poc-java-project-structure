package com.example.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class UserConfiguration {

    @Bean
    UserRepository userRepository(DataSource dataSource) {
        return new UserRepository(dataSource);
    }

    @Bean
    UserService userService(UserRepository userRepository) {
        return new UserServiceServer(userRepository);
    }
}