package com.example.shared;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean
    DataSource dataSource(Environment environment) {
        var dataSource = new MysqlDataSource();

        dataSource.setURL(environment.getRequiredProperty("database.url", String.class));
        dataSource.setUser(environment.getRequiredProperty("database.user", String.class));
        dataSource.setPassword(environment.getRequiredProperty("database.password", String.class));

        return dataSource;
    }

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
            .locations("classpath:migrations")
            .dataSource(dataSource)
            .load();
    }
}